(function() {

	app = {
		map: null,

		init: function() {
	    	geo.current(function(loc) {
	    		console.log("Current position: " + loc.coords.latitude + ", " + loc.coords.longitude);
				var myOptions = {
		        	center: new google.maps.LatLng(loc.coords.latitude, loc.coords.longitude),
		        	zoom: 14,
		        	mapTypeId: google.maps.MapTypeId.ROADMAP
		    	};
		    	app.map = new google.maps.Map(document.getElementById("map"), myOptions);
	    	})	 
		}	
	};

	geo = {
		// returns the current coordiantes
		current: function(callback) {
			if(navigator.geolocation) {				
				navigator.geolocation.getCurrentPosition(callback);
			}
			else {
				alert("You need a modern browser to be able to retrieve your current GPS position");
				callback({"loc": { "coords": { "latitude": 1, "longitude": 1 }}});
			}
		}			
	}
	
	$(document).ready(function() {
		console.log("Map initialized");
		app.init();
	});
})();