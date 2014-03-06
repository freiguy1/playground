var systemAjax = {
    getAll : function() {
	return $.ajax({
	    type: "GET",
	    url: "/clone/api/systems"
	})
    }
}



var beltAjax = {
    getAll : function() {
	return $.ajax({
	    type: "GET",
	    url: "/clone/api/belts"
	})
    },
    hasRats : function(id) {
	return $.ajax({
	    type: "POST",
	    url: "/clone/api/belts/" + id + "/hasRats"
	})
    },
    isEmpty : function(id) {
	return $.ajax({
	    type: "POST",
	    url: "/clone/api/belts/" + id + "/isEmpty"
	})
    },
    clear: function(id) {
	return $.ajax({
	    type: "PUT",
	    url: "/clone/api/belts/" + id + "/clear"
	})
    }
}


getTime = function() {
    return $.ajax({
	type: "GET",
	url: "/clone/api/time"
    })
}
