console.log("api.js script reports connection!");

function getLikers(elem){
	let id = $(elem).find("small").attr('id');
	console.log("getLikers input = "+id);
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : '/api/likers',
		// Using of JSON.stringify is here IMPORTANT
		data : JSON.stringify({
			'tweetid' : id,
			'size' : 5
		}),
		dataType : "json",
		complete : function(data) {
			console.log(data);
//			console.log("JSON.stringify(data) = "+ JSON.stringify(data));
//			console.log("data.readyState = "+ data.readyState);
			let response = data.responseJSON;
			console.log("response = "+ data.responseJSON);
			let output = "";
			$("#"+id).html(output);
			for (var i = 0; i < response.length; i++) {
				output += "<a href='/user/"+ response[i]["id"] +"/tweets'>" + response[i]["firstName"] + "</a>" + ( i == response.length -1 ? ".":", ");
				console.log("output = "+output);
				
			}
			$("#"+id).html(output);

		}
	});
}

function hideLikers(elem){
	let id = $(elem).find("small").attr('id');
	console.log("hideLikers input = "+id);
	let output = "who likes your tweet?";
	$("#"+id).html(output);
}

//function getOurJSON(input) {
//	let id = input.id;
//	console.log("input = "+id);
//	
//	$.ajax({
//		type : "POST",
//		contentType : "application/json",
//		url : '/api/ourJSON',
//		// Using of JSON.stringify is here IMPORTANT
//		data : JSON.stringify({
//			'tweetid' : id,
//			'size' : 5
//		}),
//		dataType : "json",
//		complete : function(data) {
//			console.log("JSON.stringify(data) = "+ JSON.stringify(data));
//			let response = data.responseJSON;
//			let output = "there should be some output here";
//			for (var i = 0; i < response.length; i++) {
//				output += response[i]["firstName"] + ",";
//
//			}
//			$("#"+id).html(output);
//
//		}
//	});
//}
//
//function getAuto(input) {
//	let id = input;
//	console.log("input = "+id);
//	let encodedURL = "http://localhost:8080/likers/search/getLikers?tweetid="
//			+ encodeURIComponent(id) + "&page=0&size=5";
//
//	$.getJSON(encodedURL, function(data) {
//
//		let response = data._embedded.likers;
//		let output = "";
//		for (var i = 0; i < response.length; i++) {
//			output += response[i]["firstName"] + " ";
//
//		}
//		$("#"+id).html(output);
//
//	});
//
//}
