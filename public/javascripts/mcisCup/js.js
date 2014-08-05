var viewModel = new CupViewModel();

$(function () {
    ko.applyBindings(viewModel);
    viewModel.refreshData();
});

function CupViewModel() {
    var self = this;

    self.teams = ko.observableArray([]);
    self.competitions = ko.observableArray([]);
    self.results = ko.observableArray([]);
    self.leader = ko.observable();
    self.nextCompetitionInfo = ko.observable();
    self.month = ko.computed(function(){
        var currentTime = new Date();
        var month = [];
        month[0] = "January";
        month[1] = "February";
        month[2] = "March";
        month[3] = "April";
        month[4] = "May";
        month[5] = "June";
        month[6] = "July";
        month[7] = "August";
        month[8] = "September";
        month[9] = "October";
        month[10] = "November";
        month[11] = "December";
        return month[currentTime.getMonth()];
    });

    self.isTieForLeader = ko.computed(function() {
      return self.teams().length >= 2 && self.teams()[0].totalPoints() === self.teams()[1].totalPoints()
    });

    self.tiePoints = ko.observable();

    self.tieDisplay = ko.computed(function(){
        var text = '';
        if(self.leader() !== undefined && self.leader().totalPoints() !== undefined) {
            self.tiePoints(self.leader().totalPoints());
            var tiedTeams = ko.utils.arrayFilter(self.teams(), function(item) {
                return item.totalPoints() === self.tiePoints();
            });
            var teamNames = ko.utils.arrayMap(tiedTeams, function(item) {
                return item.name();
            });
            text = teamNames.join(', ');
        }
        return text;
    });

    self.nextCompetitionDetailsClicked = function() {
        $('#nextCompetitionDetailsModal').modal()
    };

    self.refreshData = function() {
        ajax.info().done(function(data){
            ko.mapping.fromJS(data.competitions, {}, self.competitions);
            ko.mapping.fromJS(data.results, {}, self.results);
    	    self.nextCompetitionInfo(ko.mapping.fromJS(data.nextCompetitionInfo));
            self.teams.removeAll();
            $.each(data.teams, function(index, team){
               self.teams.push(new Team(team.teamId, team.name, team.captainName));
            });
            self.teams.sort(function(left, right){
                return left.totalPoints() == right.totalPoints() ? 0 : (left.totalPoints() > right.totalPoints() ? -1 : 1);
            });
            self.leader(self.teams()[0]);
            $('.score-popover').popover();
        });
    };
}

function Team(teamId, name, captainName) {
    var self = this;
    self.teamId = teamId;
    self.name = ko.observable(name);
    self.captainName = ko.observable(captainName);
    self.results = ko.computed(function(){
        var list = [];
        $.each(viewModel.competitions(), function(i, c){
            var result = {
                hasValue: ko.observable(false)
            };
            $.each(viewModel.results(), function(j, r){
                if(r.competitionId() === c.competitionId() && r.teamId() == teamId){
                    result = {
                        hasValue: ko.observable(true),
                        result: r
                    };
                }
            });
            list.push(result);
        })
        return list;
    });
    self.totalPoints = ko.computed(function(){
        var counter = 0;
        $.each(self.results(), function(i, r){
            if(r.hasValue())
                counter += r.result.pointsEarned();
        });
        return counter;
    });
}


/*--------------- AJAX -------------*/

var ajax = {
    info : function() {
        return $.ajax({
            type: "GET",
            url: "/mcisCup/info"
        });
    }
};

