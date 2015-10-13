$(function() {
	
	$('#ajaxPost').on('click', function(evt) {
		var data = JSON.stringify({ 
			"greet": 'Ciao',
			"name": 'Mondo',
			"year": "2010",
			"hours": "1,5",
			"booleano": "true",
			"checkbox": getCampoCheckbox(),
			"data": getCampoData(),
			"range": { "min": 2, "max": 10 },
			"ranges": [{ "min": 2, "max": 10 }, 
			           { min: 10, max: 18 }, 
			           { min: 90, max: 100 }]
		});
		ajax('json', '../api/demo/helloWorld.post', data, 'post', 'application/json');
	});
	
	function getCampoData() { return $("#data").val(); }
	function getCampoCheckbox() { return $("#checkbox").prop('checked'); }
	
	$('#ajaxError').on('click', function(evt) {
		var data = { greet: 'Ciao', name: 'Mondo', "min": 2, "max": 10, year: 2010, hours: "1,5", booleano: true };
		ajax('json', '../api/demo/helloWorld.error', data, 'get');
	});

	$('#ajaxGet').on('click', function(evt) {
		var data = { greet: 'Ciao', name: 'Mondo', "min": 2, "max": 10, year: 2010, hours: "1,5", booleano: true };
		ajax('json', '../api/demo/helloWorld.getJson~Json', data, 'get');
	});
	
	$('#ajaxJsonP').on('click', function(evt) {
		var data = { greet: 'Ciao', name: 'Mondo', "min": 2, "max": 10, year: 2010, hours: "1,5", booleano: true };
		ajax('jsonp', '../api/demo/helloWorld.getJsonP~JsonP', data, 'get');
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