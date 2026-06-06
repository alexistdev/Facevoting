<?php
defined('BASEPATH') or exit('No direct script access allowed');?>
<!-- Content Wrapper. Contains page content -->
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<div class="container-fluid">
			<div class="row mb-2">
				<div class="col-sm-6">
					<h1>Daftar Perolehan Suara</h1>
				</div>
				<div class="col-sm-6">
					<ol class="breadcrumb float-sm-right">
						<li class="breadcrumb-item"><a href="<?= base_url('Member'); ?>">Home</a></li>
						<li class="breadcrumb-item active">Hasil</li>
					</ol>
				</div>
			</div>
		</div><!-- /.container-fluid -->
	</section>

	<!-- Main content -->
	<section class="content">

		<!-- Default box -->
		<div class="card card-dark">
			<div class="card-header">
				<h3 class="card-title">Daftar Hasil Perolehan Suara</h3>

			</div>
			<div class="card-body">
				<table id="tablehasil" class="table table-bordered table-hover" style="width: 100%">
					<thead>
					<tr>
						<th class="text-center">No</th>
						<th class="text-center">Kategori</th>
						<th class="text-center">Nomor Urut</th>
						<th class="text-center">Calon Ketua</th>
						<th class="text-center">Calon Wakil</th>
						<th class="text-center">Perolehan Suara</th>
					</tr>
					</thead>
					<tbody>
					<?php
					$no = 1;
					foreach ($dataPaslon as $rowPaslon) :
						$gambar1 = filter_output($rowPaslon['photo1_paslon']);
						$gambar2 = filter_output($rowPaslon['photo2_paslon']);
						?>
						<tr>
							<td class="text-center"><?= filter_output($no++); ?></td>
							<td class="text-center"><?= filter_output($rowPaslon['nama_kategori']); ?></td>
							<td class="text-center"><?= filter_output($rowPaslon['judul_paslon']); ?></td>
							<td class="text-center"><img src="<?= base_url('gambar/'.$gambar1); ?>" width="100px" alt="<?= filter_output($rowPaslon['ketua_paslon']); ?>"/></td>
							<td class="text-center"><img src="<?= base_url('gambar/'.$gambar2); ?>" width="100px" alt="<?= filter_output($rowPaslon['wakil_paslon']); ?>"/></td>
							<td class="text-center"><?= filter_output($rowPaslon['perolehan']); ?></td>

						</tr>
					<?php endforeach; ?>
					</tbody>
				</table>
			</div>
		</div>
		<!-- /.card -->

	</section>
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->
