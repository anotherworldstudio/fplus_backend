/**
 * 좌측문자열채우기
 * @params
 *  - padLen : 최대 채우고자 하는 길이
 *  - padStr : 채우고자하는 문자(char)
 */
String.prototype.lpad = function(padLen, padStr) {
    var str = this;
    if (padStr.length > padLen) {
        console.log("오류 : 채우고자 하는 문자열이 요청 길이보다 큽니다");
        return str + "";
    }
    while (str.length < padLen)
        str = padStr + str;
    str = str.length >= padLen ? str.substring(0, padLen) : str;
    return str;
}

/**
 * 우측문자열채우기
 * @params
 *  - padLen : 최대 채우고자 하는 길이
 *  - padStr : 채우고자하는 문자(char)
 */
String.prototype.rpad = function(padLen, padStr) {
    var str = this;
    if (padStr.length > padLen) {
        console.log("오류 : 채우고자 하는 문자열이 요청 길이보다 큽니다");
        return str + "";
    }
    while (str.length < padLen)
        str += padStr;
    str = str.length >= padLen ? str.substring(0, padLen) : str;
    return str;
}


function setYn(srcChkId, srcId){
	var srcChk = $("#" + srcChkId).is( ":checked") ? 'Y' : 'N';
	$("#" + srcId).val(srcChk);
}

function setChecked(srcChkId, srcId){
	var isChecked = $("#" + srcId).val() == 'Y' ;    	
    var srcChk = $("#" + srcChkId).attr('checked', isChecked);
}

function setActive(srcChkId, srcId){
	var srcChk = $("#" + srcChkId).is( ":checked") ? '9999' : '0000';
	$("#" + srcId).val(srcChk);
}

function setActiveChecked(srcChkId, srcId){
	var isChecked = $("#" + srcId).val() == '9999' ;
	if(isChecked){
		var srcChk = $("#" + srcChkId).attr('checked', 'checked');
	}
}

function showModal(msg, isConfirm, fn){
    var str = "";
    str = str + " <div class='modal fade' tabindex='-1' role='dialog' aria-hidden='true' id='_modal'>       " ;
    str = str + "   <div class='modal-dialog modal-sm'>                                                     " ;
    str = str + "     <div class='modal-content'>                                                           " ;
	str = str + "                                                                                           " ;
    str = str + "       <div class='modal-header'>                                                          " ;
    str = str + "         <h6 class='modal-title' id='myModalLabel2'>알림</h6>                                " ;
    str = str + "         <button type='button' class='close' data-dismiss='modal' aria-label='Close'>      " ;
	str = str + "         <span aria-hidden='true'>×</span>                                                 " ;
    str = str + "         </button>                                                                         " ;
    str = str + "       </div>                                                                              " ;
    str = str + "       <div class='modal-body'>                                                            " ;
    str = str + "         <h6>" + msg + "</h6>                                                              " ;
    str = str + "       </div>                                                                              " ;
    str = str + "       <div class='modal-footer'>                                                          " ;
    
    if(isConfirm){
    	str = str + "         <button type='button' class='btn btn-primary' id='_modal_confirm' >확인</button>     " ;
    }
    
	str = str + "		  <button type='button' class='btn btn-secondary' data-dismiss='modal'>닫기</button>  " ;                       
    str = str + "       </div>                                                                              " ;
    str = str + "     </div>                                                                                " ;
    str = str + "   </div>                                                                                  " ;
    str = str + " </div>                                                                                    " ;
	

	
	if($("#_modal").length){
		$("#_modal").remove();
	}	
	
	$("body").append(str);
	
	if(isConfirm){
		$("#_modal_confirm").on("click", function(){
			fn();
			$('#_modal').modal("hide"); //닫기
		}
		);
		 
	}
	
	$('#_modal').modal("show");	
}

function showConfirm(msg, fn){
	showModal(msg, true, fn);
}

function getPageDiv(paging){

  var pageDiv = "";
  if(paging == null) return;
  
  var previosPage = paging.startPage - 1;
  var perPage = paging.displayPageNum;
  var nextPage = paging.endPage + 1;
  var startPage = paging.startPage;
  var endPage = paging.endPage;
  var currPage = paging.page;	  
	  
  pageDiv = pageDiv + "<div class='row'>                                                ";
  pageDiv = pageDiv + "  <div class='btn-group' role='group'>                           ";
  
  if(paging.prev){    	  
  	pageDiv = pageDiv + "    <button type='button' class='btn btn-secondary' onClick='search(" + previosPage + ", " + perPage + ")'>이전</button>  ";
  }else{
  	pageDiv = pageDiv + "    <button type='button' class='btn btn-secondary' disabled>이전</button>  ";
  }
  
  for( i = startPage ; i <= endPage ; i++ ){
	  if(currPage == i){
		 pageDiv = pageDiv + "<button type='button' class='btn btn-secondary' disabled>" + i + "</button>";    		  
	  }else{    		  
	  	pageDiv = pageDiv + "<button type='button' class='btn btn-secondary' onClick='search(" + i + ", " + perPage + ")'>" + i + "</button>   ";
	  }
	  
  }
  
  if(paging.next){    	  
   	pageDiv = pageDiv + "    <button type='button' class='btn btn-secondary' onClick='search(" + nextPage + ", " + perPage + ")'>다음</button>  ";
   }else{
   	pageDiv = pageDiv + "    <button type='button' class='btn btn-secondary' disabled>다음</button>  ";
   }
  
  
  pageDiv = pageDiv + "  </div>                                                         ";
  pageDiv = pageDiv + "</div>                                                           ";
  
  return pageDiv;
}


	
	
function getYoutubeId(url) {
    var tag = "";
    if(url)  {
        var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
        var matchs = url.match(regExp);
        if (matchs) {
            tag = matchs[7];
        }
        return tag;
    }
}	

function checkFeature(chkBox){
	
	console.log("checkFeature : " + chkBox.name + " checked : " + chkBox.checked);
	
	var fId = "";	
	var fVal = "";
	var feature = null;
	var otherValue = null;
	for(var i = 0 ; i < 8 ; i++){
		fId = "featureList" + i + ".otherCode";
		feature = document.getElementById(fId);
		
		fVal = feature.value;
		console.log("feature code : " + fVal);
		if(fVal == chkBox.name){			
			otherValue = document.getElementById("featureList" + i + ".otherValue");			
			if(chkBox.checked){
			  otherValue.value = 1;	
			}else{
		      otherValue.value = 0;
			}
			
			break;
		}
	}
}

var categoryData = null;

function setCategoryData(inputCategory, category1, category2, optional){
	var selected = "";
	
	if( inputCategory == null) { // 최초 조회 시점
		$('#category1').empty();
		var isFirst = true;
		var option = null;
		
		if(optional){
			option = $("<option value='' >선택하세요</option>");			
			$('#category1').append(option);
		}
		
		$.each(categoryData, function(index, item) {
			if( isFirst ){				
				inputCategory = item.middleId;
				isFirst = false;
				console.log(item.middleName);
			}
			
			option = $("<option value='" + item.middleId + "' >"+item.middleName+"</option>");
			
			$('#category1').append(option);
		});
	}
	
	if(category1 != null){
		inputCategory = category1; 
		$('#category1').val(category1).prop("selected","selected");
	}		
	
	
	var firstVal = $('#category2 option:eq(0)').val();
 
    $('#category2').empty();
    selected = "";
	
	if(optional || firstVal == ""){
		option = $("<option value='' >선택하세요</option>");			
		$('#category2').append(option);
	}
		
	$.each(categoryData, function(index, item) {
		if(inputCategory == item.middleId){
			console.log("대상 분류 : " + item.middleName);
			
			$.each(item.subClassList, function(index, item2) {
				// console.log(item2.subName);
				
				option = $("<option value='" + item2.subId + "' >"+item2.subName+"</option>");
				$('#category2').append(option);
				
			});
		}
	});
	
	console.log("category2 : " + category2);
	if(category2 != null){
		$('#category2').val(category2).prop("selected","selected");
	}
}

function category1Change(){
	var inputCategory = $('#category1 option:selected').val();
	
	setCategoryData(inputCategory);	
}

function getListCategoryData(category1, category2){
	reqCategoryData(category1, category2, true);
}

function getCategoryData(category1, category2){
	reqCategoryData(category1, category2);
}

function reqCategoryData(category1, category2, optional){
	var param = {};  
	
	$.ajax({
        url : "/w1/product/get_classdata",
        data : param,
        type : 'post',
        contentType:"application/json; charset=UTF-8",
        dataType:"json",
        async: false,
        success : function(data){
        	categoryData = data;
        	setCategoryData(null, category1, category2, optional);
        },
        
        error : function(e){
        	console.log(e);
        }
    });
	
}

function getPointCategoryData(category1, category2){
	reqPointCategoryData(category1, category2);
}

function reqPointCategoryData(category1, category2, optional){
	var param = {};  
	
	$.ajax({
        url : "/w1/product/get_point_class_data",
        data : param,
        type : 'post',
        contentType:"application/json; charset=UTF-8",
        dataType:"json",
        async: false,
        success : function(data){
        	categoryData = data;
        	setCategoryData(null, category1, category2, optional);
        },
        
        error : function(e){
        	console.log(e);
        }
    });
	
}



