var publicAjax = {
    getEntries : function() {
        return $.ajax({
            type: "GET",
            url: "/soup/entries"
        })
    },
    getVotesByEntry : function(entryId) {
        return $.ajax({
            type: "GET",
            url: "/soup/entries/" + entryId + "/votes"
        })
    },
    addVote : function(entryId, voterName, comment) {
        return $.ajax({
            type: "POST",
            url: "/soup/entries/" + entryId + "/votes",
            data: JSON.stringify({
                voterName: voterName,
                comment: comment
            }),
            contentType: "application/json"
        })
    }
}

var adminAjax = {
    getEntries : function() {
        return $.ajax({
            type: "GET",
            url: "/soup/adminEntries"
        })
    },
    clearVotes : function() {
        return $.ajax({
            type: "DELETE",
            url: "/soup/votes"
        })
    },
    addEntry: function(newEntry) {
        return $.ajax({
            type: "POST",
            url: "/soup/entries",
            data: JSON.stringify(newEntry),
            contentType: "application/json"
        });
    },
    updateEntry: function(entryId, newEntry) {
        return $.ajax({
            type: "PUT",
            url: "/soup/entries/" + entryId,
            data: JSON.stringify(newEntry),
            contentType: "application/json"
        });
    },
    deleteEntry: function(entryId) {
        return $.ajax({
            type: "DELETE",
            url: "/soup/entries/" + entryId
        })
    }
}
