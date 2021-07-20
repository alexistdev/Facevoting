<?php
defined('BASEPATH') or exit('No direct script access allowed');
?>
<!DOCTYPE html>
<html>
<?php $this->load->view('template/v_header') ?>

<body class="hold-transition sidebar-mini pace-danger">

<!-- Site wrapper -->
<div class="wrapper">
<?php $this->load->view('template/v_navbar') ?>
<?php $this->load->view('template/v_sidebar') ?>
<?php $this->load->view('konten/k_paslon') ?>
<?php $this->load->view('template/v_footer') ?>
</div>

<!-- jQuery -->
<script src="<?= base_url('vendor/almasaeed2010/adminlte') ?>/plugins/jquery/jquery.min.js"></script>
<!-- Bootstrap 4 -->
<script src="<?= base_url('vendor/almasaeed2010/adminlte') ?>/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<!-- AdminLTE App -->
<script src="<?= base_url('vendor/almasaeed2010/adminlte') ?>/dist/js/adminlte.min.js"></script>
<!-- pace-progress -->
<script src="<?= base_url('vendor/almasaeed2010/adminlte') ?>/plugins/pace-progress/pace.min.js"></script>
<!-- DataTables -->
<script src="<?= base_url('vendor/almasaeed2010/adminlte') ?>/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="<?= base_url('vendor/almasaeed2010/adminlte') ?>/plugins/datatables-bs4/js/dataTables.bootstrap4.min.js"></script>
<script src="<?= base_url('vendor/almasaeed2010/adminlte') ?>/plugins/datatables-responsive/js/dataTables.responsive.min.js"></script>
<script src="<?= base_url('vendor/almasaeed2010/adminlte') ?>/plugins/datatables-responsive/js/responsive.bootstrap4.min.js"></script>
<script>
	$(document).ready( function () {
		$('#tabelPaslon').DataTable({
			"pageLength": 10,
			"responsive": true,
			"lengthChange": false,
			"searching":false,
		});
	} );
	/** Saat tombol modal diklik */
	$(document).on("click", "#tombolHapus", function () {
		var token = $(this).data('id');
		var newUrl = "<?= base_url('Paslon/hapus/'); ?>" + token;
		$("#urlKunci").attr('href', newUrl);
	});
	$(window).bind("load", function() {
		window.setTimeout(function() {
			$(".alert").fadeTo(500, 0).slideUp(500, function() {
				$(this).remove();
			});
		}, 2000);
	});
</script>
</body>

</html>
