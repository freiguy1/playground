
$(function () {
    initViewModel()
    ko.applyBindings(viewModel)

    $('#addEntryModal').on('shown.bs.modal', function () {
        $('#addEntryNameInput').focus();
    })

    $('#updateEntryModal').on('shown.bs.modal', function () {
        $('#updateEntryNameInput').focus();
    })

    onEnter('#addEntryNameInput, #addEntryNumberInput, #addEntryChefNameInput', function() {
        viewModel.addEntryModalClicked();
    });
    onEnter('#updateEntryNameInput, #updateEntryNumberInput, #updateEntryChefNameInput', function() {
        viewModel.updateEntryModalClicked();
    });
    
})

function onEnter(query, func) {
    $(query).keypress(function (event) {
        if (event.which == 13)
            func()
    })
}


initViewModel = function() {
    refreshData()
}

refreshData = function() {
    viewModel.loading(true)
    adminAjax.getEntries().done(function(data) {
        $.each(data, function(index, value) {
            if(!value.description)
                value.description = null
            value.editUrl = "http://" + window.location.host + "/chili/" + value.uuid

        })
        data.sort(function(left, right) {
            return left.number - right.number
        })
        ko.mapping.fromJS(data, {}, viewModel.entries)
        $
        viewModel.loading(false)
    });
}

var viewModel = {
    loading: ko.observable(true),
    entries: ko.observableArray([]),
    addEntry: {
        name: ko.observable(""),
        number: ko.observable(""),
        chefName: ko.observable(""),
        spicyLevel: ko.observable(""),
        description: ko.observable(""),
        displayError: ko.observable(false)
    },
    updateEntry: {
        entryId: ko.observable(),
        name: ko.observable(""),
        number: ko.observable(""),
        chefName: ko.observable(""),
        spicyLevel: ko.observable(""),
        description: ko.observable(""),
        displayError: ko.observable(false)
    },
    confirmModal: {
        yesText: ko.observable(""),
        noText: ko.observable(""),
        messageText: ko.observable(""),
        clicked: function(result) { }
    }
}

viewModel.nextEntryNumber = ko.computed(function() {
    var highest = 1
    var numbers = $.map(viewModel.entries(), function(value) {
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

viewModel.addEntryClicked = function() {
    viewModel.addEntry.name("")
    viewModel.addEntry.number(viewModel.nextEntryNumber())
    viewModel.addEntry.chefName("")
    viewModel.addEntry.description("")
    viewModel.addEntry.spicyLevel("Mild")

    viewModel.addEntry.displayError(false)

    $('#addEntryModal').modal()
}

viewModel.addEntryModalClicked = function() {
    viewModel.addEntry.displayError(false)
    var description = viewModel.addEntry.description()
    if(!description || description.match(/^ *$/) !== null) {
        description = null
    }
    adminAjax.addEntry({
        name: viewModel.addEntry.name(),
        number: parseInt(viewModel.addEntry.number()),
        chefName: viewModel.addEntry.chefName(),
        spicyLevel: viewModel.addEntry.spicyLevel(),
        description: description
    }).done(function() {
        $('#addEntryModal').modal('hide')
        refreshData()
    }).fail(function() {
        viewModel.addEntry.displayError(true)
    })
}


viewModel.updateEntryClicked = function(data) {
    viewModel.updateEntry.entryId(data.entryId())
    viewModel.updateEntry.name(data.name())
    viewModel.updateEntry.number(data.number())
    viewModel.updateEntry.chefName(data.chefName())
    viewModel.updateEntry.description(data.description())
    viewModel.updateEntry.spicyLevel(data.spicyLevel())

    viewModel.updateEntry.displayError(false)

    $('#updateEntryModal').modal()
}

viewModel.updateEntryModalClicked = function() {
    viewModel.updateEntry.displayError(false)
    var description = viewModel.updateEntry.description()
    if(!description || description.match(/^ *$/) !== null) {
        description = null
    }
    adminAjax.updateEntry(viewModel.updateEntry.entryId(), {
        name: viewModel.updateEntry.name(),
        number: parseInt(viewModel.updateEntry.number()),
        chefName: viewModel.updateEntry.chefName(),
        spicyLevel: viewModel.updateEntry.spicyLevel(),
        description: description
    }).done(function() {
        $('#updateEntryModal').modal('hide')
        refreshData()
    }).fail(function() {
        viewModel.updateEntry.displayError(true)
    });
}

viewModel.deleteEntryClicked = function(data) {
    showConfirmModal(
        "Delete", 
        "Cancel", 
        "All votes for this chili will be removed.", 
        function(result) {
        if(result) {
            viewModel.loading(true)
            adminAjax.deleteEntry(data.entryId()).done(function() {
                refreshData()
            }).fail(function() {
                alert('There was a problem deleting that chili')
                viewModel.loading(false)
            })
        } else {

        }
    })
}

viewModel.clearVotesClicked = function(data) {
    showConfirmModal(
        "Clear", 
        "Cancel", 
        "All votes will be cleared.", 
        function(result) {
        if(result) {
            adminAjax.clearVotes().done(function() {
            }).fail(function() {
                alert('There was a problem clearing all votes')
            })
        } else {

        }
    })
}

showConfirmModal = function(
    yesText,
    noText,
    messageText,
    func) {
    viewModel.confirmModal.yesText(yesText)
    viewModel.confirmModal.noText(noText)
    viewModel.confirmModal.messageText(messageText)
    viewModel.confirmModal.clicked = func
    $("#confirmModal").modal()
}
