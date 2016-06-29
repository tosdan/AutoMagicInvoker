$(function() {
	var date = {
		"2": null,
		"3": null
	};
	
	function getDate(n) {
		return data[n] ||  "2016-06-28T01:23:11.442Z+02:00";
	}
	
	function getFormParams() {
		return JSON.stringify({ 
			"greet": 'Ciao',
			"name": 'Mondo',
			"year": "2010",
			"hours": "1,5",
			"time": "23:54:22",
			"time2": "20:33:10",
			"data2": getDate(2),
			"booleano": "true",
			"checkbox": getCampoCheckbox(),
			"data": getCampoData(true),
			"data3": getDate(3),
			"range": { "min": 2, "max": 10 },
			"ranges": [{ "min": 2, "max": 10 }, 
			           { min: 10, max: 18 }, 
			           { min: 90, max: 100 }]
		});
	}
	
	function getCampoData(trueDate) {
		var value = $("#data").val();
		var date = new Date(value);
		date.setHours(24);
		return trueDate ? date : value; 
	}
	function getCampoData3() { return new Date($("#data3").val()); }
	function getCampoCheckbox() { return $("#checkbox").prop('checked'); }

	$('#ajaxPost').on('click', function(evt) {
		var data = getFormParams();
		ajax('json', '../api/demo/helloWorld.post', data, 'post', 'application/json');
	});
	
	$('#ajaxError').on('click', function(evt) {
		var data = { greet: 'Ciao', name: 'Mondo', "min": 2, "max": 10, year: 2010, hours: "1,5", booleano: true };
		ajax('json', '../api/demo/helloWorld.error', data, 'get');
	});
	
	$('#ajaxException').on('click', function(evt) {
		var data = { greet: 'Ciao', name: 'Mondo', "min": 2, "max": 10, year: 2010, hours: "1,5", booleano: true };
		ajax('json', '../api/demo/helloWorld.exception', data, 'get');
	});
	
	$('#ajaxInexistent').on('click', function(evt) {
		var data = { greet: 'Ciao', name: 'Mondo', "min": 2, "max": 10, year: 2010, hours: "1,5", booleano: true };
		ajax('json', '../api/demo/helloWorld.inexistent', data, 'get');
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
			console.log(new Date(data.helloExt.unaData));
			console.log(new Date(data.hello.data2), new Date(data.hello.data3));
			data["2"] = new Date(data.hello.data2);
			data["3"] = new Date(data.hello.data3);
		});
	}
	
});