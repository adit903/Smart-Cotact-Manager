console.log("Welcome")

const toggleSidebar = () =>{
	
	if($(".sidebar").is(":visible")){
		
		$(".sidebar").css("display","none")
		$(".content").css("marging-left","0%")
	}
	else{
		$(".sidebar").css("display","block")
		$(".content").css("marging-left","20%")
	}
}

const search = () => {
	
	let query= $("#search-input").val();
	
	if(query=="")
	{
		$(".search-result").hide();
	}
	else{
		console.log(query);
		
		//sending request to server
		
		let url=`http://localhost:8282/search/${query}`;
		
		fetch(url).then((response) =>{
		return response.json();
		}).then((data)=>{
			//console.log(data);
			
			let text=`<div class='list-group'>`;
			
			data.forEach((contact)=>{
				text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`
			});
			
			 text+=`</div>`;
			 
			 $(".search-result").html(text);
			 $(".search-result").show();
			
		});
		
		$(".search-result").show();
	}
	
	//console.log("searching...");
};
