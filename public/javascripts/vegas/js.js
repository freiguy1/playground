$(function () {
    initViewModel()
    ko.applyBindings(viewModel)
    $('[data-toggle="tooltip"]').tooltip();

   $('#addTeamNameInput, #addTeamCaptainNameInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.handleCreateTeam()
   })

   $('#editTeamNameInput, #editTeamCaptainNameInput, #editTeamMilesInput').keypress(function (event) {
        if (event.which == 13)
            viewModel.handleEditTeam()
   })
})

var colors = [
   "#b3e2cd",
   "#fdcdac",
   "#cdb5e8",
   "#f4cae4",
   "#bee376",
   "#fff2ae",
   "#f1e2cc"
];

function initViewModel(){
    //refreshData();
}

function refreshData(){
    ajax.getTeams().done(function(data){
        viewModel.teams.removeAll();
        $.each(data, function(index, team){
           viewModel.teams.push(new Team(team.id, team.name, team.captainName, team.miles));
        })
        $('[data-toggle="tooltip"]').tooltip();
    })
}

var viewModel = {
    teams: ko.observableArray([]),

    addTeam: {
       name: ko.observable(""),
       captainName: ko.observable("")
    },

    editTeam: {
        teamId: ko.observable(""),
        name: ko.observable(""),
        captainName: ko.observable(""),
        miles: ko.observable(0.0)
    },

    confirmModalModel: {
       text: ko.observable(""),
       yesText: ko.observable(""),
       noText: ko.observable(""),
       onYes: function(){},
       onNo: function(){}
    },

}

viewModel.createTeamClicked = function(){
    viewModel.addTeam.name("")
    viewModel.addTeam.captainName("")
    $('#addTeamModal').modal('show')
}

viewModel.handleCreateTeam = function(){
    ajax.addTeam({ name: viewModel.addTeam.name(), captainName: viewModel.addTeam.captainName() }).done(function(){
        $('#addTeamModal').modal('hide')
        refreshData()
    })
}

viewModel.editTeamClicked = function(team){
    viewModel.editTeam.name(team.name());
    viewModel.editTeam.captainName(team.captainName());
    viewModel.editTeam.miles(team.miles());
    viewModel.editTeam.teamId(team.teamId);

    $('#editTeamModal').modal('show')
}

viewModel.handleEditTeam = function(){
    ajax.editTeam(viewModel.editTeam.teamId(), { name: viewModel.editTeam.name(), captainName: viewModel.editTeam.captainName(), miles: parseFloat(viewModel.editTeam.miles()) }).done(function(){
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

function Team(teamId, name, captainName, miles) {
    var me = this
    me.teamId = teamId 
    me.name = ko.observable(name)
    me.captainName = ko.observable(captainName)
    me.miles = ko.observable(miles)
    me.percent = ko.computed(function() {
        return (Math.round(parseFloat(miles) / 1775 * 1000) / 10).toString() + "%";
    });
    me.color = colors[teamId % colors.length];
    me.tooltip = ko.observable(me.percent() + " of the way to Vegas");
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
    getTeams : function() {
        return $.ajax({
            type: "GET",
            url: "/vegas/teams"
        })
    },
    addTeam: function(team) {
        return $.ajax({
            type: "POST",
            url: "/vegas/teams",
            data: JSON.stringify(team),
            contentType: "application/json"
        })
    },
    editTeam: function(teamId, team) {
        return $.ajax({
            type: "PUT",
            url: "/vegas/teams/" + teamId,
            data: JSON.stringify(team),
            contentType: "application/json"
        })
    },
    deleteTeam: function(teamId) {
        return $.ajax({
            type: "DELETE",
            url: "/vegas/teams/" + teamId
        })
    }
}

