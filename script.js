$(function() {
	
	$('#ajaxPost').on('click', function(evt) {
		var data = JSON.stringify({ "greet": 'Ciao', "name": 'Mondo', "year": "2010", "hours": "1,5", "booleano": "true",
			"range": { "min": 2, "max": 10 },
			"ranges": [{ "min": 2, "max": 10 }, 
			           { min: 10, max: 18 }, 
			           { min: 90, max: 100 }]
		});
		ajax('json', 'action/demo/helloWorld.post', data, 'post', 'application/json');
	});
	
	$('#ajaxError').on('click', function(evt) {
		var data = { greet: 'Ciao', name: 'Mondo', "min": 2, "max": 10, year: 2010, hours: "1,5", booleano: true };
		ajax('json', 'action/demo/helloWorld.error', data, 'get');
	});

	$('#ajaxGet').on('click', function(evt) {
		var data = { greet: 'Ciao', name: 'Mondo', "min": 2, "max": 10, year: 2010, hours: "1,5", booleano: true };
		ajax('json', 'action/demo/helloWorld.getJson~Json', data, 'get');
	});
	
	$('#ajaxJsonP').on('click', function(evt) {
		var data = { greet: 'Ciao', name: 'Mondo', "min": 2, "max": 10, year: 2010, hours: "1,5", booleano: true };
		ajax('jsonp', 'action/demo/helloWorld.getJsonP~JsonP', data, 'get');
	});
	
	function ajax(type, url, data, method, contentType) {
		console.log(method.toUpperCase() + " ("+ type + ") -> " + url + '...');
		var options = { 
			url: url,
			dataType: type,
			data: data,
			jsonp: 'callback',
			type: method ,
			contentType: contentType ? contentType : 'application/x-www-form-urlencoded'
		};
		
		var req = $.ajax(options);
		
		req.then(function(data) {
			console.log(data);
		});
	}
	
});