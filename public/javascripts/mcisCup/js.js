$(function () {
    initViewModel()
    ko.applyBindings(viewModel)
})



function initViewModel(){
    refreshData()
}

function refreshData(){
    ajax.info().done(function(data){
        ko.mapping.fromJS(data.competitions, {}, viewModel.competitions)
        ko.mapping.fromJS(data.results, {}, viewModel.results)
	viewModel.nextCompetitionInfo(ko.mapping.fromJS(data.nextCompetitionInfo))
        viewModel.teams.removeAll()
        $.each(data.teams, function(index, team){
           viewModel.teams.push(new Team(team.teamId, team.name, team.captainName))
        })
        viewModel.teams.sort(function(left, right){
            return left.totalPoints() == right.totalPoints() ? 0 : (left.totalPoints() > right.totalPoints() ? -1 : 1)
        })
        viewModel.leader(viewModel.teams()[0]);
        $('.score-popover').popover();
    })
}

var viewModel = {
    teams: ko.observableArray([]),
    competitions: ko.observableArray([]),
    results: ko.observableArray([]),
    leader: ko.observable(),
    nextCompetitionInfo: ko.observable()
}

viewModel.nextCompetitionDetailsClicked = function(){
    $('#nextCompetitionDetailsModal').modal()
}

viewModel.isTieForLeader = ko.computed(function(){
    return viewModel.teams().length >= 2 && viewModel.teams()[0].totalPoints() === viewModel.teams()[1].totalPoints()
})

function Team(teamId, name, captainName) {
    var me = this
    me.teamId = teamId 
    me.name = ko.observable(name)
    me.captainName = ko.observable(captainName)
    me.results = ko.computed(function(){
        var list = []
        $.each(viewModel.competitions(), function(i, c){
            var result = { hasValue: ko.observable(false) }
            $.each(viewModel.results(), function(j, r){
                if(r.competitionId() === c.competitionId() && r.teamId() == teamId){
                    result = { hasValue: ko.observable(true), result: r }
                }
            })
            list.push(result)
        })
        return list
    })
    me.totalPoints = ko.computed(function(){
        var counter = 0;
        $.each(me.results(), function(i, r){
            if(r.hasValue())
            counter += r.result.pointsEarned()
        })
        return counter;
    })
}


/*--------------- AJAX -------------*/

var ajax = {
    info : function() {
	return $.ajax({
	    type: "GET",
	    url: "/mcisCup/info"
	})
    }
}

