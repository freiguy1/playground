var viewModel = new ChiliViewModel();

$(function(){
    viewModel.initialize();
    ko.applyBindings(viewModel);

    $('.modal').on('hidden.bs.modal', function () {
        viewModel.thankYou(false);
    })
});

function ChiliViewModel() {
    var self = this;
    self.loading = ko.observable(true);
    self.thankYou = ko.observable(false);
    self.entries = ko.observableArray([]);
    self.initialize = function(){
        self.loading(true);
        publicAjax.getEntries().done(function(result){
            $.each(result, function(index, value) {
                if(!value.description)
                    value.description = null;
            });
            result.sort(function(left, right) {
                return left.number - right.number;
            });
            ko.mapping.fromJS(result, {}, viewModel.entries);
            self.loading(false);
        });
    };
    self.voteFor = ko.observable(new Entry('', '', '', ''));

    self.setVoteFor = function(entry) {
        self.voteFor(new Entry(entry.name(), entry.number(), '', ''));
    };

    self.submitVote = function() {
        var voteInfo = ko.toJS(self.voteFor);

        publicAjax.addVote(voteInfo.entryNumber, voteInfo.voterName, voteInfo.voterComments)
            .done(function(){
                self.showThankYou();
            });
    };

    self.showThankYou = function(){
        self.thankYou(true);
        setTimeout(self.hideThankYou, 2000);
    };

    self.hideThankYou = function(){
        $('.modal').modal('hide');
    };
}

function Entry(entryName, entryNumber, voterName, voterComments) {
    var self = this;
    self.entryName = ko.observable(entryName);
    self.entryNumber = ko.observable(entryNumber);
    self.voterName = ko.observable(voterName);
    self.voterComments = ko.observable(voterComments);
}