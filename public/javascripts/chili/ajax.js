var publicAjax = {
    getEntries : function() {
        return $.ajax({
            type: "GET",
            url: "/chili/entries"
        })
    },
    getVotesByEntry : function(entryId) {
        return $.ajax({
            type: "GET",
            url: "/chili/entries/" + entryId + "/votes"
        })
    },
    addVote : function(entryId, voterName, comment) {
        return $.ajax({
            type: "POST",
            url: "/chili/entries/" + entryId + "/votes",
            data: JSON.stringify({
                voterName: voterName,
                comment: comment
            }),
            contentType: "application/json"
        })
    }
}

var adminAjax = {
    addEntry: function(newEntry) {
        return $.ajax({
            type: "POST",
            url: "/chili/entries",
            data: JSON.stringify(newEntry),
            contentType: "application/json"
        });
    },
    updateEntry: function(entryId, newEntry) {
        return $.ajax({
            type: "PUT",
            url: "/chili/entries/" + entryId,
            data: JSON.stringify(newEntry),
            contentType: "application/json"
        });
    },
    deleteEntry: function(entryId) {
        return $.ajax({
            type: "DELETE",
            url: "/chili/entries/" + entryId
        })
    }
}
