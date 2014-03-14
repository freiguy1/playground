$(function () {
    initViewModel()
    ko.applyBindings(viewModel)

    $('#transactionAmountInput, #transactionNoteInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.transactClicked()
    })

})

function initViewModel(){
   ajax.info().done(function(result){ fillFromResponse(result) }) 
}

var viewModel = {
    balanceAmount: ko.observable(),
    transactions: ko.observableArray([])
}

viewModel.balanceAmountDisplay = ko.computed(function(){
    if(viewModel.balanceAmount() || viewModel.balanceAmount() === 0)
        return getDollarString(viewModel.balanceAmount())
    else
	return ""
})

viewModel.transactClicked = function(data, event) {
    ajax.addTransaction($('#transactionAmountInput').val(), $('#transactionNoteInput').val()).done(function(result){
	$('#transactionAmountInput, #transactionNoteInput').val('')
	fillFromResponse(result)	
    })
}

viewModel.boughtClicked = function(amount, note) {
    ajax.addTransaction(amount, note).done(function(result){
	fillFromResponse(result)	
    })
}

function fillFromResponse(response){
    viewModel.balanceAmount(response.amount)
    viewModel.transactions.removeAll()
    $.each(response.transactions.reverse(), function(index, value){
	viewModel.transactions.push(new Transaction(value.id, value.amount, value.time, value.note))
    })

    // Fill the line graph
    var currentTime = new Date()
    var lineGraphData = [[currentTime.getTime(), response.amount]]
    if(response.transactions.length > 0 && response.transactions[0].time > currentTime.getTime())
	lineGraphData = []
    var movingBalance = response.amount
    var minBalance = 0
    var maxBalance = 10
    $.each(response.transactions, function(index, value){
	lineGraphData.push([value.time, movingBalance])
	maxBalance = movingBalance > maxBalance ? movingBalance : maxBalance
	minBalance = movingBalance < minBalance ? movingBalance : minBalance
	movingBalance = movingBalance - value.amount;
    })
    if(lineGraphData.length > 1){
	$.plot($('#line-graph'), [lineGraphData], {
	    yaxis: { min: minBalance, max: maxBalance,
		tickFormatter: function(val, axis) { return "$" + val }
	    },
	    xaxis: { mode: "time" , twelveHourClock: true, timezone: "browser" },
	    hoverable: true
	})
    }

    // Fill in the pie graph
    var pieGraphMap = []
    var pieGraphData = []
    $.each(response.transactions, function(index, value) {
	if(value.note && value.amount < 0){
	    if(pieGraphMap[value.note] % 1 === 0)
		pieGraphData[pieGraphMap[value.note]] = { label: value.note, data: pieGraphData[pieGraphMap[value.note]].data - value.amount } 
	    else
		pieGraphMap[value.note] = pieGraphData.push({ label: value.note, data: 0 - value.amount }) - 1
	}
    })
    if(pieGraphData.length > 0)
	$.plot($('#pie-graph'), pieGraphData, {
	    series: {
		pie: {
		    show: true,
		    label: {
		      formatter: function(label, series){
			return '<div style="text-align: center; font-size: 8pt;">' + label + '<br /> $' + series.data[0][1].toFixed(2) + ', ' + series.percent.toFixed(1) + '%</div>'
		      }
		    }
		}
	    },
	    legend: {
		show: false
	    }
	})
}

function Transaction(id, amount, time, note) {
    var me = this
    me.id = id
    me.amount = ko.observable(amount)
    me.time = ko.observable(time)
    me.note = ko.observable(note)
    me.timeDisplay = ko.computed(function (){
	return getDateString(new Date(me.time()))
    })
    me.amountDisplay = ko.computed(function (){
	return getDollarString(me.amount())	
    })
}

function getDollarString(amount){
    return "$" + amount.toFixed(2)
}

function getDateString(date){
    var minute = date.getMinutes()
    var hour = date.getHours()
    var ampm = hour >= 12 ? 'pm' : 'am'
    hour = hour % 12
    hour = hour ? hour: 12
    minute = minute < 10 ? '0'+minute : minute
    return date.getMonth() + "-" + date.getDate() + "-" + date.getFullYear() + "   " + hour + ":" + minute + " " + ampm
}

/*--------------- AJAX -------------*/

var ajax = {
    info : function() {
	return $.ajax({
	    type: "GET",
	    url: "/balance/info"
	})
    },
    addTransaction : function(amount, note) {
	var noteQueryVar;
	if(note)
	    noteQueryVar = "&note=" + escape(note)
	else
	    noteQueryVar = ""
	return $.ajax({
	    type: "POST",
	    url: "/balance?amount=" + amount + noteQueryVar
	})
    }
}


/*

function initViewModel() {
    var allSystemListRequest = systemAjax.getAll()
    var allBeltListRequest = beltAjax.getAll()
    var getTimeRequest = getTime()
    $.when(allSystemListRequest, allBeltListRequest, getTimeRequest).done(function(allSystemListResponse, allBeltListResponse, getTimeResponse){
	$.each(allSystemListResponse[0], function(index, system){
	    var beltArr = []
	    $.each(allBeltListResponse[0], function(index, belt){
		if(belt.systemId === system.systemId)
		    beltArr.push(new Belt(belt.beltId, belt.planetNum, belt.beltNum, belt.hasRats, belt.lastChecked, belt.lastStatusChanged))
	    })
	    viewModel.systems.push(new System(system.systemId, system.name, (index === 0), beltArr))
	})
    viewModel.offset = (new Date(getTimeResponse[0].time).getTime()) - (new Date().getTime())	

    })
}

var viewModel = {
    currentTime: ko.observable(new Date().getTime() + this.offset),
    systems: ko.observableArray([]),
    addSystemName: ko.observable(""),
    addBeltPlanetNum: ko.observable(""),
    addBeltBeltNum: ko.observable(""),
    offset: 0
};

systemBackgroundColor = ko.computed(function (data) {

})
*/
