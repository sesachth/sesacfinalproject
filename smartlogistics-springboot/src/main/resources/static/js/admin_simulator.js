$(document).ready(function() {
	$.ajax({
        url: 'http://localhost:8000/api/v1/boxes',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            // 서버에서 받은 데이터로 HTML 업데이트
            var html = '<ul>';
            $.each(data.boxes, function(index, box) {
                html += '<li>Box ' + (index + 1) + ': ' +
						'Spec: ' + box.spec + '</li>'
						'Width: ' + box.width + ', ' +
                        'Depth: ' + box.depth + ', ' +
                        'Height: ' + box.height + ', ';
            });
            html += '</ul>';
            $('#boxList').html(html);
        },
        error: function(xhr, status, error) {
            console.error('Error:', error);
            $('#boxList').html('<p>Error loading data</p>');
        }
    });
});