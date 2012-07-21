(function() {

	app = {
	    // Google Maps API object
		map: null,

		// center
		marker: null,

		// array of markers currently displayed
		sightings: Array(),

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
		    	google.maps.event.addListener(app.map, 'idle', app.controller.boundingBoxChanged);

		    	// enable buttons
		    	$('#button-report').button().click(app.controller.reportClicked);
	    	})
		},

		controller: {
		    addMarker: function(lat, lng, text) {
                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(lat, lng),
                    map: app.map,
                    animation: google.maps.Animation.DROP
                });

                // info window, if text provided
                if(text != null) {
                    var infoWindow = new google.maps.InfoWindow({
                        content: text
                    });

                    google.maps.event.addListener(marker, 'click', function() {
                        infoWindow.open(app.map, marker);
                    });
                }

                app.sightings[lat + "-" + lng] = marker;
                return(marker);
		    },

		    deleteAllMarkers: function() {
                for(i in app.sightings) {
                    app.sightings[i].setMap(null);
                }
		    },

		    markerExists: function(lat, lng) {
		        return(app.sightings[lat + "-" + lng] != null);
		    },

		    boundingBoxChanged: function() {
                // place the marker in the center
		    	app.marker.setPosition(app.map.getCenter());
		    	// and refresh the list of markers
		    	var ne = app.map.getBounds().getNorthEast();
		    	var sw = app.map.getBounds().getSouthWest();
		    	$.ajax({
		    	    url: "/services/area?lat1=" + ne.lat() + "&lng1=" + ne.lng() + "&lat2=" + sw.lat() + "&lng2=" + sw.lng(),
		    	    type: "GET",
		    	    success: function(response) {
		    	        console.log("New markers received: " + response.data.length);

                        // add the new markings if they don't exist yet
                        for(i in response.data) {
                            if(!app.controller.markerExists(response.data[i].lat, response.data[i].lng))
                                app.controller.addMarker(
                                    response.data[i].lat,
                                    response.data[i].lng,
                                    "Created on: " + response.data[i].timestamp.unix
                                );
                        }
		    	    },
		    	    error: function() {
		    	        console.log("Error loading marker data")
		    	    }
		    	})
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