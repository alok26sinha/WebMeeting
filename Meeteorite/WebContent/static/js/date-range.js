$(document).ready(
		function() {
			$('#startDate').datepicker({
//				format : "yyyy-mm-dd",
				dateFormat : "yy-mm-dd"
			}).on('changeDate', function(ev) {
//				$('#endDate').val($('#startDate').val());
				$('#startDate').datepicker('hide');
			});
			$('#startDate').click(function() {
				$('#startDate').datepicker('show');
			});
//			$('#endDate').datepicker({
//				format : "yyyy-mm-dd"
//			}).on(
//					'changeDate',
//					function(ev) {
//						var startDateString = $('#startDate').val();
//						var dateParts = startDateString.split("-");
//						var startDate = new Date(dateParts[0],
//								(dateParts[1] - 1), dateParts[2]);
//						var endDateString = $('#endDate').val();
//						dateParts = endDateString.split("-");
//						var endDate = new Date(dateParts[0],
//								(dateParts[1] - 1), dateParts[2]);
//
//						if (endDate < startDate) {
//							alert("The end date must be after the start date");
//						} else {
//							$('#endDate').datepicker('hide');
//						}
//					});
//			$('#endDate').click(function() {
//				$('#endDate').datepicker('show');
//			});

//			$('#startTime').change(function() {
//				if($('#startTime').val() > $('#endTime').val()){
//					$('#endTime').val($('#startTime').val());
//				}
//			});
//
//			$('#endTime').change(function() {
//				if($('#startTime').val() > $('#endTime').val()){
//					$('#startTime').val($('#endTime').val());
//				}
//			});
		});