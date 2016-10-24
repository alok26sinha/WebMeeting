var backgroundSave = function(actionName, idField, propertyName) {
	var dirty = false;
	var backgroundSaveWaiting = false;
	var backgroundSaveId = null;
	var value = null;

	function getTargetFor(event) {
		var targ;
		if (!event)
			var event = window.event;

		if (event.target)
			targ = event.target;
		else if (event.srcElement)
			targ = event.srcElement;

		if (targ.nodeType == 3) // defeat Safari bug
			targ = targ.parentNode;

		return targ;
	}

	function backgroundSave() {
		save();
		backgroundSaveWaiting = false;
	}

	function save() {
		if (dirty) {
			var id = $(idField).val();
			var data = {
				'id' : id
			};
			data[propertyName] = value;
			$.post(actionName, data);
			dirty = false;
		}
	}

	return {
		keyUp : function(event) {

			var targ = getTargetFor(event);
			value = targ.value;

			dirty = true;

			if (!backgroundSaveWaiting) {
				backgroundSaveWaiting = true;
				backgroundSaveId = setTimeout(function(){backgroundSave();}, 3000);
			}

		},
		leaving : function(event) {
			clearTimeout(backgroundSaveId);
			backgroundSaveWaiting = false;
			save();
		}
	};

};

var backgroundSave2 = function(actionName, id, propertyName) {
	var dirty = false;
	var backgroundSaveWaiting = false;
	var backgroundSaveId = null;
	var value = null;

	function getTargetFor(event) {
		var targ;
		if (!event)
			var event = window.event;

		if (event.target)
			targ = event.target;
		else if (event.srcElement)
			targ = event.srcElement;

		if (targ.nodeType == 3) // defeat Safari bug
			targ = targ.parentNode;

		return targ;
	}

	function backgroundSave() {
		save();
		backgroundSaveWaiting = false;
	}

	function save() {
		if (dirty) {
			
			//Lookup the id if required
			if( typeof id === 'string' && id.indexOf("#") == 0)
				id = $(idField).val();
			
			var data = {
				'id' : id
			};
			data[propertyName] = value;
			$.post(actionName, data);
			dirty = false;
		}
	}

	return {
		keyUp : function(event) {

			var targ = getTargetFor(event);
			value = targ.value;

			dirty = true;

			if (!backgroundSaveWaiting) {
				backgroundSaveWaiting = true;
				backgroundSaveId = setTimeout(function(){backgroundSave();}, 3000);
			}

		},
		leaving : function(event) {
			clearTimeout(backgroundSaveId);
			backgroundSaveWaiting = false;
			save();
		}
	};

};

var dropDownUtilities = function() {
	var hidingFromBlur = false;
	return {
		toggle: function(){
			if( hidingFromBlur)
				return;

			$('.parked-thoughts').find('.drop-down').show('slow', function() {
			    if($('.parked-thoughts').find('.drop-down:first textarea').is(':visible')){
			    	var drop= $('.parked-thoughts').find('.drop-down:first textarea');
			    	var elem = drop[0];
			    	var elemLen = elem.value.length;
			        // For IE Only
			        if (document.selection) {
			            // Set focus
			            elem.focus();
			            // Use IE Ranges
			            /*var oSel = document.selection.createRange();
			            // Reset position to 0 & then set at end
			            oSel.moveStart('character', -elemLen);
			            oSel.moveStart('character', elemLen);
			            oSel.moveEnd('character', 0);
			            oSel.select(); 
			            
			            Removed because was causing blur therefore closing parked thoughts window.
			            TO DO: Fix later 
			            */
			            
			            
			        }
			        else if (elem.selectionStart || elem.selectionStart == '0') {
			            // Firefox/Chrome
			            elem.selectionStart = elemLen;
			            elem.selectionEnd = elemLen;
			            elem.focus();
			        }
			    	//drop.focus();
			    }
			 });			
		},
		hide: function(){
			hidingFromBlur = true;
			$('.parked-thoughts').find('.drop-down').hide('slow', function() {
			   hidingFromBlur = false;
			 });
		}
	};
};


var dropDownUtilities2 = function() {
	var hidingFromBlur = false;
	return {
		toggle: function(){
			if( hidingFromBlur)
				return;

			$('.private-notes').find('.drop-down').show('slow', function() {
			    if($('.private-notes').find('.drop-down:first textarea').is(':visible')){
			    	var drop= $('.private-notes').find('.drop-down:first textarea');
			    	var elem = drop[0];
			    	var elemLen = elem.value.length;
			        // For IE Only
			        if (document.selection) {
			            // Set focus
			            elem.focus();
			            // Use IE Ranges
			            /*var oSel = document.selection.createRange();
			            // Reset position to 0 & then set at end
			            oSel.moveStart('character', -elemLen);
			            oSel.moveStart('character', elemLen);
			            oSel.moveEnd('character', 0);
			            oSel.select(); 
			            
			            Removed because was causing blur therefore closing parked thoughts window.
			            TO DO: Fix later 
			            */
			            
			            
			        }
			        else if (elem.selectionStart || elem.selectionStart == '0') {
			            // Firefox/Chrome
			            elem.selectionStart = elemLen;
			            elem.selectionEnd = elemLen;
			            elem.focus();
			        }
			    	//drop.focus();
			    }
			 });			
		},
		hide: function(){
			hidingFromBlur = true;
			$('.private-notes').find('.drop-down').hide('slow', function() {
			   hidingFromBlur = false;
			 });
		}
	};
};

