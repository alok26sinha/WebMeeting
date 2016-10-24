
function applyEmailHover () {
	$(".email-hover").hover(
			function(event){
				var tooltipX = $(this).position().left;
				var tooltipY = $(this).position().top - 16 - $(this).css("paddingTop");
				var email = $(this).attr("email");
				$(this).append($("<div id='tooltip' style='position: absolute;'>" + email + "</div>"));
				$("div#tooltip").css({top: tooltipY, left: tooltipX});
			}, 
			function(event){
				$(this).find("div#tooltip").remove();
		});	
}

$(document).ready(function(){
	applyEmailHover();
});

if (!String.prototype.trim) {
	String.prototype.trim=function(){return this.replace(/^\s+|\s+$/g, '');};
}

function validateEmail(email) {  
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/; 
    return re.test(email); 
}  

function goToURL(val) {
	location.href = val;
}

// ---------------------------------------------------------------------------------------------------------------
// Attachments
function ajaxFileUpload(meetingId)
{
	$("#loading")
	.ajaxStart(function(){
		$(this).show();
	})
	.ajaxComplete(function(){
		$(this).hide();
	});

	$.ajaxFileUpload
	(
		{
			url:'../support/File!upload.action',
			secureuri:true,
			fileElementId:'fileToUploadId',
			dataType: 'json',
			data:{id:meetingId},
			success: function (data, status)
			{
				if(typeof(data.error) != 'undefined')
				{
					if(data.error != '')
					{
						alert(data.error + " " + data.msg);
					}else
					{
						populateFileList(data);
					}
				}
			},
			error: function (data, status, e)
			{
				alert(e);
			}
		}
	);
	
	return false;

}

function ajaxFileDelete(meetingId){
	if( confirm('Are you sure you want to delete this file?') ){
		$.ajaxFileUpload(
				{
					url:'../support/File!delete.action',
					secureuri:true,
					dataType: 'json',
					data:{id:meetingId},
					success: function (data, status)
					{
						if(typeof(data.error) != 'undefined')
						{
							if(data.error != '')
							{
								alert(data.error);
							}else
							{
								populateFileList(data);
							}
						}
					},
					error: function (data, status, e)
					{
						alert(e);
					}
				}
			);
	}
}

function updateFileList(meetingId){
	$.ajaxFileUpload(
		{
			url:'../support/File!list.action',
			secureuri:true,
			dataType: 'json',
			data:{id:meetingId},
			success: function (data, status)
			{
				if(typeof(data.error) != 'undefined')
				{
					if(data.error != '')
					{
						alert(data.error);
					}else
					{
						populateFileList(data);
					}
				}
			},
			error: function (data, status, e)
			{
				alert(e);
			}
		}
	);
}

function populateFileList(data){
	var ul = $("#fileListId");
	ul.empty();
	
	$.each(data.file, function(index, value){
		//ul.append('<li>' + value.name +  '</li>');
		ul.append('<li><a href="../support/File!download.action?id=' + value.id + '" target="_blank">' + value.name + '</a>&nbsp;<i class="icon-remove"></i>');
		ul.find("i:last").click( function() { ajaxFileDelete(value.id); return false; });
	});
	
	ul.append('<li id="loading" style="display:none;"><img src="../assets/img/spinner.gif" ></li>');	
}