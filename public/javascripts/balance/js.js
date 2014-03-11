$(function () {
    initViewModel()
    ko.applyBindings(viewModel)
})

function initViewModel(){
   ajax.info().done(function(result){ fillFromResponse(result) }) 
}

var viewModel = {
    balanceAmount: ko.observable(),
    transactions: ko.observableArray([])
}

viewModel.balanceAmountDisplay = ko.computed(function(){
    if(viewModel.balanceAmount())
        return "$" + viewModel.balanceAmount().toFixed(2)
    else
	return ""
})

viewModel.transactClicked = function(data, event) {
    ajax.addTransaction($('#transactionAmountInput').val(), $('#transactionNoteInput').val()).done(function(result){
	fillFromResponse(result)	
    })
}

function fillFromResponse(response){
    viewModel.balanceAmount(response.amount)
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
