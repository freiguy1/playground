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

   $('#editCompetitionNameInput, #editCompetitionInstructionsInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.handleEditCompetition()
   })

   $('#editTeamNameInput, #editTeamCaptainNameInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.handleEditTeam()
   })
   
   $('#editResultPointsEarnedInput, #editResultNotesInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.handleEditResult()
   })
})



function initViewModel(){
    refreshData();
}

function refreshData(){
    ajax.info().done(function(data){
        ko.mapping.fromJS(data.competitions, {}, viewModel.competitions);
        ko.mapping.fromJS(data.results, {}, viewModel.results);
        viewModel.teams.removeAll();
        $.each(data.teams, function(index, team){
           viewModel.teams.push(new Team(team.teamId, team.name, team.captainName));
        })
        viewModel.bannerModalModel.name(data.nextCompetitionInfo.name);
        viewModel.bannerModalModel.details(data.nextCompetitionInfo.details);
        viewModel.bannerModalModel.month(data.nextCompetitionInfo.month);
        $('.score-popover').popover();
    })
}

var viewModel = {
    teams: ko.observableArray([]),
    competitions: ko.observableArray([]),
    results: ko.observableArray([]),

    addTeamName: ko.observable(""),
    addTeamCaptainName: ko.observable(""),

    editTeam: ko.observable({
        name: ko.observable(""),
        captainName: ko.observable("")
    }),

    confirmModalModel: {
       text: ko.observable(""),
       yesText: ko.observable(""),
       noText: ko.observable(""),
       onYes: function(){},
       onNo: function(){}
    },

    bannerModalModel: {
       name: ko.observable(""),
       details: ko.observable(""),
       month: ko.observable("")
    },
    
    addCompetitionName: ko.observable(""),
    addCompetitionInstructions: ko.observable(""),

    editCompetition: ko.observable({
        name: ko.observable(""),
        instructions: ko.observable("")
    }),
    
    editResultTeam: ko.observable(),
    editResultCompetition: ko.observable(),
    editResultPointsEarned: ko.observable(""),
    editResultNotes: ko.observable("")
}

viewModel.updateBannerClicked = function() {
   $('#bannerModal').modal('show');
}

viewModel.bannerModalModel.onSave = function() {
    $('#bannerModal').modal('hide');
    ajax.nextCompetitionInfo(
       viewModel.bannerModalModel.name(),
       viewModel.bannerModalModel.details(),
       viewModel.bannerModalModel.month());
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

viewModel.editTeamClicked = function(team){
    viewModel.editTeam(team)
    $('#editTeamModal').modal('show')
}

viewModel.handleEditTeam = function(){
    ajax.editTeam(viewModel.editTeam().teamId, { name: viewModel.editTeam().name(), captainName: viewModel.editTeam().captainName() }).done(function(){
        $('#editTeamModal').modal('hide')
        refreshData()
    })
}

viewModel.deleteTeam = function(team){
   showConfirmModal(
      "Are you sure you want to delete '" + team.name() + "'?",
      "Yes",
      "No",
      function() {
         $('#confirmModal').modal('hide');
         ajax.deleteTeam(team.teamId).done(function(){
             refreshData();
         });
      },
      function() {});
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

viewModel.editCompetitionClicked = function(competition){
    viewModel.editCompetition(competition)
    $('#editCompetitionModal').modal('show')    
}

viewModel.handleEditCompetition = function(){
    ajax.editCompetition(viewModel.editCompetition().competitionId(), 
        { name: viewModel.editCompetition().name(), instructions: viewModel.editCompetition().instructions() })
    .done(function(){
        $('#editCompetitionModal').modal('hide')    
        refreshData()
    })  
}

viewModel.deleteCompetition = function(comp){
   showConfirmModal(
      "Are you sure you want to delete '" + comp.name() + "'?",
      "Yes",
      "No",
      function() {
         $('#confirmModal').modal('hide');
         ajax.deleteCompetition(comp.competitionId()).done(function(){
             refreshData()
         })
      },
      function() {});
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

function showConfirmModal(text, yesText, noText, onYes, onNo) {
   viewModel.confirmModalModel.text(text);
   viewModel.confirmModalModel.yesText(yesText);
   viewModel.confirmModalModel.noText(noText);
   viewModel.confirmModalModel.onYes = onYes;
   viewModel.confirmModalModel.onNo = onNo;
   $('#confirmModal').modal('show');
}


/*--------------- AJAX -------------*/

var ajax = {
    info : function() {
        return $.ajax({
            type: "GET",
            url: "/mcisCup/info"
        })
    },
    nextCompetitionInfo : function(name, details, month) {
        return $.ajax({
            type: "PUT",
            url: "/mcisCup/nextCompetitionInfo",
            contentType: "application/json",
            data: JSON.stringify({name: name, details: details, month: month})
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
    editTeam: function(teamId, team) {
        return $.ajax({
            type: "PUT",
            url: "/mcisCup/teams/" + teamId,
            data: JSON.stringify(team),
            contentType: "application/json"
        })
    },
    deleteTeam: function(teamId) {
        return $.ajax({
            type: "DELETE",
            url: "/mcisCup/teams/" + teamId
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
    editCompetition: function(competitionId, competition) {
        return $.ajax({
            type: "PUT",
            url: "/mcisCup/competitions/" + competitionId,
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

