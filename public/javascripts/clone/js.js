$(function () {
    initViewModel()
    ko.applyBindings(viewModel)
    window.setInterval(function () {
        viewModel.currentTime(new Date().getTime() + viewModel.offset)
    }, 250);

    $('#addBeltModal').on('shown.bs.modal', function () {
        $('#addBeltPlanetNumInput').focus();
    })
    $('#addBeltBeltNameInput, #addBeltPlanetNameInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.addBeltModalClicked()
    })
    $('#addSystemModal').on('shown.bs.modal', function () {
        $('#addSystemNameInput').focus();
    })
    $('#addSystemNameInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.addSystemModalClicked()
    })

})

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

viewModel.tabClicked = function (data, event) {
    data.isActive(true);
    $.each(viewModel.systems(), function (index, value) {
        if (data !== value) {
            value.isActive(false);
        }
    })
}

viewModel.activeSystem = ko.computed(function () {
    var active;
    $.each(viewModel.systems(), function (index, value) {
        if (value.isActive()) {
            active = value
        }
    })
    return active;
})

viewModel.isEmptyClicked = function (data, event) {
    beltAjax.isEmpty(data.id())
    var currentTime = viewModel.currentTime()
    if (!data.lastCheckedDisplay()) {
        data.lastCheckedTime(currentTime);
        data.statusChangedTime(currentTime);
        data.hasRats(false)
    } else {
        data.lastCheckedTime(currentTime);
        if (data.hasRats())
            data.statusChangedTime(currentTime);
        data.hasRats(false)
    }
}

viewModel.hasRatsClicked = function (data, event) {
    beltAjax.hasRats(data.id())
    var currentTime = viewModel.currentTime()
    if (!data.lastCheckedDisplay()) {
        data.lastCheckedTime(currentTime);
        data.statusChangedTime(currentTime);
        data.hasRats(true)
    } else {
        data.lastCheckedTime(currentTime);
        if (!data.hasRats())
            data.statusChangedTime(currentTime);
        data.hasRats(true)
    }
}

viewModel.clearClicked = function (data, event) {
    beltAjax.clear(data.id())
    data.lastCheckedTime(null);
}

viewModel.addSystemClicked = function () {
    $('#addSystemModal').modal()
}

viewModel.addSystemModalClicked = function () {
    $('#addSystemModal').modal('hide')
    viewModel.systems.push(new System(viewModel.addSystemName(), !viewModel.activeSystem(), []) )
    viewModel.addSystemName("");
}

viewModel.addBeltClicked = function () {
    $('#addBeltModal').modal()
}

viewModel.addBeltModalClicked = function () {
    $('#addBeltModal').modal('hide')
    viewModel.activeSystem().belts.push(new Belt(viewModel.addBeltPlanetNum(), viewModel.addBeltBeltNum(), false, null, null))
    viewModel.addBeltPlanetNum("");
    viewModel.addBeltBeltNum("");
}

viewModel.removeSystemClicked = function (data, event) {
    if (viewModel.activeSystem() === data && viewModel.systems().length > 1) {
        if (viewModel.systems.indexOf(data) === viewModel.systems().length - 1) {
            viewModel.tabClicked(viewModel.systems()[viewModel.systems().length - 2])
        }
        else {
            viewModel.tabClicked(viewModel.systems()[viewModel.systems.indexOf(data) + 1])
        }

        viewModel.systems.remove(data)
    }
    else {
        viewModel.systems.remove(data)
    }
}

viewModel.removeBeltClicked = function (data, event) {
    viewModel.activeSystem().belts.remove(data)
}

function System(id, name, isActive, belts) {
    var me = this
    this.id = ko.observable(id)
    this.name = ko.observable(name)
    this.belts = ko.observableArray(belts)
    this.isActive = ko.observable(isActive)
    this.backgroundColor = ko.computed(function () {
        var oldest = null
        $.each(me.belts(), function (index, value) {
            if(value.lastCheckedTime() != null && (!oldest || value.lastCheckedTime() < oldest))
                oldest = value.lastCheckedTime()
        })
        if(!oldest)
            return ""
        else
            return secToColor(Math.floor((viewModel.currentTime() - oldest)/1000))
    });
}
function Belt(id, planet, belt, hasRats, lastCheckedTime, statusChangedTime) {
    var me = this;
    this.id = ko.observable(id)
    this.planet = ko.observable(planet)
    this.belt = ko.observable(belt)
    this.hasRats = ko.observable(hasRats)
    this.lastCheckedTime = ko.observable(lastCheckedTime)
    this.statusChangedTime = ko.observable(statusChangedTime)
    this.emptyDisplay = ko.computed(function () {
        result = ""
        if (!me.hasRats() && me.lastCheckedTime() != null) {
            result = ""
            var millidiff = viewModel.currentTime() - me.statusChangedTime()
            var hours = Math.floor(millidiff / 3600000)
            var minutes = Math.floor((millidiff / 60000) % 60)
            var seconds = Math.floor((millidiff / 1000) % 60)
            if (hours >= 1) result = "" + hours + ":"
            if (hours >= 1 && minutes < 10)
                result = result + "0" + minutes + ":"
            else
                result = result + minutes + ":"
            if (seconds < 10)
                result = result + "0" + seconds
            else
                result = result + seconds
        }
        return result;
    })
    this.ratsDisplay = ko.computed(function () {
        result = ""
        if (me.hasRats() && me.lastCheckedTime() != null) {
            result = ""
            var millidiff = viewModel.currentTime() - me.statusChangedTime()
            var hours = Math.floor(millidiff / 3600000)
            var minutes = Math.floor((millidiff / 60000) % 60)
            var seconds = Math.floor((millidiff / 1000) % 60)
            if (hours >= 1) result = "" + hours + ":"
            if (hours >= 1 && minutes < 10)
                result = result + "0" + minutes + ":"
            else
                result = result + minutes + ":"
            if (seconds < 10)
                result = result + "0" + seconds
            else
                result = result + seconds
        }
        return result;
    })
    this.lastCheckedDisplay = ko.computed(function () {
        result = ""
        if (me.lastCheckedTime() != null) {
            result = ""
            var millidiff = viewModel.currentTime() - me.lastCheckedTime()
            var hours = Math.floor(millidiff / 3600000)
            var minutes = Math.floor((millidiff / 60000) % 60)
            var seconds = Math.floor((millidiff / 1000) % 60)
            if (hours >= 1) result = "" + hours + ":"
            if (hours >= 1 && minutes < 10)
                result = result + "0" + minutes + ":"
            else
                result = result + minutes + ":"
            if (seconds < 10)
                result = result + "0" + seconds
            else
                result = result + seconds
        }
        return result;
    })
    this.backgroundColor = ko.computed(function () {
        if (!me.lastCheckedTime()) {
            return "white"
        } else {
            var secDiff = Math.floor((viewModel.currentTime() - me.lastCheckedTime())/1000)
            return secToColor(secDiff)
        }
    })
}

secToColor = function (sec) {
    if (sec > 1800)
        return "#ebbdbf"
    else if (sec > 1680)
        return "#ffb8b6"
    else if (sec > 1440)
        return "#ffc6b5"
    else if (sec > 1260)
        return "#ffd2b5"
    else if (sec > 1080)
        return "#ffe2b6"
    else if (sec > 900)
        return "#fbeab4"
    else if (sec > 720)
        return "#f8edb5"
    else if (sec > 540)
        return "#f5f1b4"
    else if (sec > 360)
        return "#ccfab2"
    else if (sec > 180)
        return "#b3fdb0"
    else if (sec <= 180)
        return "#b9f1c0"
}
