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
    self.voteFor = ko.observable(new Entry(0, '', '', '', ''));

    self.setVoteFor = function(entry) {
        self.voteFor(new Entry(entry.entryId(), entry.name(), entry.number(), '', ''));
    };

    self.submitVote = function() {
        var voteInfo = ko.toJS(self.voteFor);

        publicAjax.addVote(voteInfo.entryId, voteInfo.voterName, voteInfo.voterComments)
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

    self.addEntry = {
        name: ko.observable(""),
        number: ko.observable(""),
        chefName: ko.observable(""),
        spicyLevel: ko.observable(""),
        description: ko.observable(""),
        displayError: ko.observable(false),
        editUrl: ko.observable(""),
        addChiliConfirmation: ko.observable(false)
    };
    
    self.addEntryClicked = function() {
        self.addEntry.name("")
        self.addEntry.number(viewModel.nextEntryNumber())
        self.addEntry.chefName("")
        self.addEntry.description("")
        self.addEntry.spicyLevel("Mild")

        self.addEntry.displayError(false)
        self.addEntry.editUrl("")
        self.addEntry.addChiliConfirmation(false)

        $('#addEntryModal').modal()
    }
    
    self.nextEntryNumber = ko.computed(function() {
        var highest = 1
        var numbers = $.map(self.entries(), function(value) {
            return value.number()
        })
        $.each(numbers, function(index, value) {
            if(value > highest)
                highest = value
        })
        var result = highest + 1
        for(i = highest; i > 0; i--) {
            if($.inArray(i, numbers) == -1) 
                result = i;
        }
        return result
    })

    self.addEntryModalClicked = function() {
        self.addEntry.displayError(false)
        var description = viewModel.addEntry.description()
        if(!description || description.match(/^ *$/) !== null) {
            description = null
        }
        adminAjax.addEntry({
            name: self.addEntry.name(),
            number: parseInt(self.addEntry.number()),
            chefName: self.addEntry.chefName(),
            spicyLevel: self.addEntry.spicyLevel(),
            description: description
        }).done(function(data) {
            self.addEntry.editUrl("http://" + window.location.host + "/chili/" + data.uuid)
            self.addEntry.addChiliConfirmation(true)
            self.initialize()
        }).fail(function() {
            self.addEntry.displayError(true)
        })
    }

}

function Entry(entryId, entryName, entryNumber, voterName, voterComments) {
    var self = this;
    self.entryId = ko.observable(entryId);
    self.entryName = ko.observable(entryName);
    self.entryNumber = ko.observable(entryNumber);
    self.voterName = ko.observable(voterName);
    self.voterComments = ko.observable(voterComments);
}
