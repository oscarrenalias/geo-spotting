(function() {

	app = {
		map: null,
		marker: null,

		init: function() {
	    	geo.current(function(loc) {
	    		console.log("Current position: " + loc.coords.latitude + ", " + loc.coords.longitude);
				var myOptions = {
		        	center: new google.maps.LatLng(loc.coords.latitude, loc.coords.longitude),
		        	zoom: 14,
		        	mapTypeId: google.maps.MapTypeId.ROADMAP
		    	};

		    	// create the map
		    	app.map = new google.maps.Map(document.getElementById("map"), myOptions);

		    	// drop the marker in the center
		    	app.marker = new google.maps.Marker({
                    position: app.map.getCenter(),
                    map: app.map
		    	});

		    	// set up the events
		    	google.maps.event.addListener(app.map, 'bounds_changed', app.controller.boundingBoxChanged);

		    	// enable buttons
		    	$('#button-report').button().click(app.controller.reportClicked);
	    	})
		},

		controller: {
		    boundingBoxChanged: function() {
                // place the marker in the center
		    	app.marker.setPosition(app.map.getCenter());
		    },

		    reportClicked: function() {
		        var center = app.map.getCenter();
		        $.ajax({
                    url: "/services/report/" + center.lat() + "," + center.lng(),
                    type: "POST",
                    success: function(data) {
                        console.log(data);
                        window.alert("Done");
                    }
		        });
		    }
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