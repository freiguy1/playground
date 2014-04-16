$(function () {
    initViewModel()
    ko.applyBindings(viewModel)

   $('#addCompetitionNameInput, #addCompetitionInstructionsInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.handleCreateCompetition()
    })

   $('#addTeamNameInput, #addTeamCaptainNameInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.handleCreateTeam()
    })

   $('#editResultPointsEarnedInput, #editResultNotesInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.handleEditResult()
    })
    


})



function initViewModel(){
    refreshData()
}

function refreshData(){
    ajax.info().done(function(data){
	ko.mapping.fromJS(data.competitions, {}, viewModel.competitions)	
	ko.mapping.fromJS(data.results, {}, viewModel.results)
	viewModel.teams.removeAll()
	$.each(data.teams, function(index, team){
	   viewModel.teams.push(new Team(team.teamId, team.name, team.captainName))
	})
    })
}

var viewModel = {
    teams: ko.observableArray([]),
    competitions: ko.observableArray([]),
    results: ko.observableArray([]),

    addTeamName: ko.observable(""),
    addTeamCaptainName: ko.observable(""),

    addCompetitionName: ko.observable(""),
    addCompetitionInstructions: ko.observable(""),
    
    editResultTeam: ko.observable(),
    editResultCompetition: ko.observable(),
    editResultPointsEarned: ko.observable(""),
    editResultNotes: ko.observable("")
}

viewModel.createTeamClicked = function(){
    viewModel.addTeamName("")
    viewModel.addTeamCaptainName("")
    $('#addTeamModal').modal('show')
}

viewModel.handleCreateTeam = function(){
    ajax.addTeam({ name: viewModel.addTeamName(), captainName: viewModel.addTeamCaptainName() }).done(function(){
	$('#addTeamModal').modal('hide')
	refreshData()
    })
}

viewModel.createCompetitionClicked = function(){
    viewModel.addCompetitionName("")
    viewModel.addCompetitionInstructions("")
    $('#addCompetitionModal').modal('show')    
}

viewModel.handleCreateCompetition = function(){
    ajax.addCompetition({ name: viewModel.addCompetitionName(), instructions: viewModel.addCompetitionInstructions() }).done(function(){
	$('#addCompetitionModal').modal('hide')    
	refreshData()
    })	
}

viewModel.deleteCompetition = function(comp){
    ajax.deleteCompetition(comp.competitionId()).done(function(){
	refreshData()
    })
}

viewModel.editResultClicked = function(teamId, competitionId){
    $.each(viewModel.competitions(), function(i, c){
	if(c.competitionId() == competitionId)
	    viewModel.editResultCompetition(c)
    })
    $.each(viewModel.teams(), function(i, t){
	if(t.teamId == teamId)
	    viewModel.editResultTeam(t)
    })
    viewModel.editResultPointsEarned("")
    viewModel.editResultNotes("")
    $.each(viewModel.results(), function(i, r){
	if(r.teamId() == teamId && r.competitionId() == competitionId){
	    viewModel.editResultPointsEarned(r.pointsEarned())
	    viewModel.editResultNotes(r.notes())
	}
    })
    $('#editResultModal').modal('show')
}

viewModel.handleEditResult = function(){
    ajax.editResult(
	viewModel.editResultTeam().teamId, 
	viewModel.editResultCompetition().competitionId(), 
	{ pointsEarned: parseInt(viewModel.editResultPointsEarned()), notes: viewModel.editResultNotes() }
    ).done(function(){
	$('#editResultModal').modal('hide')
	refreshData()
    })
}


function Team(teamId, name, captainName) {
    var me = this
    me.teamId = teamId 
    me.name = ko.observable(name)
    me.captainName = ko.observable(captainName)
    me.results = ko.computed(function(){
	var list = []
	$.each(viewModel.competitions(), function(i, c){
	    var result = { hasValue: ko.observable(false), result: {teamId: ko.observable(teamId), competitionId: c.competitionId } }
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
    },
    addTeam: function(team) {
	return $.ajax({
	    type: "POST",
	    url: "/mcisCup/teams",
	    data: JSON.stringify(team),
	    contentType: "application/json"
	})
    },
    addCompetition: function(competition) {
	return $.ajax({
	    type: "POST",
	    url: "/mcisCup/competitions",
	    data: JSON.stringify(competition),
	    contentType: "application/json"
	})
    },
    deleteCompetition: function(competitionId) {
	return $.ajax({
	    type: "DELETE",
	    url: "/mcisCup/competitions/" + competitionId
	})
    },
    editResult: function(teamId, competitionId, result) {
	return $.ajax({
	    type: "PUT",
	    url: "/mcisCup/competitions/" + competitionId + "/teams/" + teamId,
	    data: JSON.stringify(result),
	    contentType: "application/json"
	})
    }
}
