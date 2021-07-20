<?php
defined('BASEPATH') or exit('No direct script access allowed');?>
<!-- Content Wrapper. Contains page content -->
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<div class="container-fluid">
			<div class="row mb-2">
				<div class="col-sm-6">
					<h1>Jenis Pemilihan Umum</h1>
				</div>
				<div class="col-sm-6">
					<ol class="breadcrumb float-sm-right">
						<li class="breadcrumb-item"><a href="<?= base_url('Member'); ?>">Home</a></li>
						<li class="breadcrumb-item active">Jenis Pemilihan</li>
					</ol>
				</div>
			</div>
		</div><!-- /.container-fluid -->
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<div class="col-md-12">
				<?php
				echo $this->session->flashdata('message1');
				echo $this->session->flashdata('message2'); ?>
			</div>
		</div>
		<!-- Default box -->
		<div class="card card-dark">
			<div class="card-header">
				<h3 class="card-title">Daftar Pemilihan</h3>
				<a href="<?= base_url('Kategori/tambah'); ?>"><button class="btn btn-sm btn-primary float-right">Tambah</button></a>
			</div>
			<div class="card-body">
				<table id="tabelKategori" class="table table-bordered table-hover" style="width: 100%">
					<thead>
					<tr>
						<th class="text-center">No</th>
						<th class="text-center">Logo</th>
						<th class="text-center">Nama Pemilihan</th>
						<th class="text-center">Status</th>
						<th class="text-center">Action</th>
					</tr>
					</thead>
					<tbody>
					<?php
						$no = 1;
						foreach ($dataKategori as $rowKategori) :
							$gambar = filter_output($rowKategori['logo_kategori']);
							$status = filter_output($rowKategori['status_kategori'])
					?>
						<tr>
							<td class="text-center"><?= filter_output($no++); ?></td>
							<td class="text-center"><img src="<?= base_url('gambar/'.$gambar); ?>" width="50%" alt="<?= filter_output($rowKategori['nama_kategori']); ?>"/></td>
							<td class="text-center"><?= filter_output($rowKategori['nama_kategori']); ?></td>
							<td class="text-center"><?= ($status == 2)? 'Berjalan': 'Ditutup'; ?></td>
							<td class="text-center">
								<a href="<?= base_url('Kategori/detail/'.filter_output($rowKategori['id_kategori'])); ?>"><button class="btn btn-primary" data-toggle="tooltip" data-placement="top" title="Info Detail"><i class="fas fa-eye"></i></button></a>
								<?php if($status == 2) { ?>
									<a href="<?= base_url('Kategori/tutup/'.filter_output($rowKategori['id_kategori'])); ?>"><button class="btn btn-danger" data-toggle="tooltip" data-placement="top" title="Tutup Pemilu"><i class="fas fa-power-off"></i></button></a>
								<?php } else { ?>
									<a href="<?= base_url('Kategori/buka/'.filter_output($rowKategori['id_kategori'])); ?>"><button class="btn btn-success" data-toggle="tooltip" data-placement="top" title="Mulai Pemilu"><i class="fas fa-play-circle"></i></button></a>
								<?php } ?>
							</td>
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
