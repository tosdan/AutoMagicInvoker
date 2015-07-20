$(function() {
	
	$('#ajaxPost').on('click', function(evt) {
		console.log('post...');
		var req = $.ajax({
			url: 'action/demo/helloWorld.post',
			data: JSON.stringify({
				"greet": 'Ciao',
				"name": 'Mondo',
				"year": "2010",
				"hours": "1,5",
				"booleano": "true",
				"range": {
					"min": 2,
					"max": 10
				}
			}),
			dataType: 'json',
			type: 'post',
			contentType: 'application/json'				
		});
		
		req.then(function(data) {
			console.log(data);
		});
	});
	

	$('#ajaxGet').on('click', function(evt) {
		console.log('get...');
		var req = $.ajax({
			url: 'action/demo/helloWorld.get',
			data: {
				greet: 'Ciao',
				name: 'Mondo',
				"min": 2,
				"max": 10,
				year: 2010,
				hours: "1,5",
				booleano: true
			},
			type: 'get'	,
			contentType: 'application/x-www-form-urlencoded'				
		});
		req.then(function(data) {
			console.log(data);
		});
	});
});