<?php
defined('BASEPATH') or exit('No direct script access allowed');?>
<!-- Content Wrapper. Contains page content -->
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<div class="container-fluid">
			<div class="row mb-2">
				<div class="col-sm-6">
					<h1>Data Album</h1>
				</div>
				<div class="col-sm-6">
					<ol class="breadcrumb float-sm-right">
						<li class="breadcrumb-item"><a href="<?= base_url('Member'); ?>">Home</a></li>
						<li class="breadcrumb-item active">Album</li>
					</ol>
				</div>
			</div>
		</div><!-- /.container-fluid -->
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<div class="col-md-12">
				<!-- Start Pesan -->
				<?= $this->session->flashdata('pesan');
				$this->session->flashdata('pesan2');
				?>
				<!-- End Pesan -->
			</div>
		</div>
		<!-- Default box -->
		<div class="card card-dark">
			<div class="card-header">
				<h3 class="card-title">Daftar Album</h3>

			</div>
			<div class="card-body">
				<table id="tabelPhoto" class="table table-bordered table-hover" style="width: 100%">
					<thead>
					<tr>
						<th class="text-center">No</th>
						<th class="text-center">Nama Album</th>
						<th class="text-center">Pemilik</th>
						<th class="text-center">Album Key</th>
						<th class="text-center">Action</th>
					</tr>
					</thead>
					<tbody>
					<?php
					$no = 1;
					foreach ($dataAlbum as $rowAlbum) :?>
						<tr>
							<td class="text-center"><?= filter_output($no++); ?></td>
							<td class="text-center"><?= filter_output($rowAlbum['nama_album']); ?></td>
							<td class="text-center"><?= filter_output($rowAlbum['nama']); ?></td>
							<td class="text-center"><?= filter_output($rowAlbum['kode_album']); ?></td>
							<td class="text-center">
								<a href="<?= base_url('Album/viewAlbum/'.$rowAlbum['id_album']); ?>"><button class="btn btn-primary"><i class="fas fa-eye"></i> View</button></a>
								<a href="<?= base_url('Album/rebuild/'.$rowAlbum['id_album']); ?>"><button class="btn btn-success"><i class="fas fa-eye"></i> Rebuild</button></a>
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
