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
			<div class="col-12">
				<?php
				echo $this->session->flashdata('message');
				?>
			</div>
			<div class="col-4 col-md-4 col-sm-12 col-xs-12">
				<div class="card card-dark">
					<div class="card-header">
						<h3 class="card-title">Data Paslon</h3>
					</div>
					<div class="card-body">
						<table id="tabelPaslon" class="table table-bordered table-hover" style="width: 100%">
							<thead>
							<tr>
								<th class="text-center">No</th>
								<th class="text-center">Paslon</th>
								<th class="text-center">Ketua</th>
								<th class="text-center">Wakil</th>
							</tr>
							</thead>
							<tbody>
								<?php
								$no = 1;
								foreach($dataPaslon as $rowPaslon ): ?>
									<tr>
										<td class="text-center"><?= $no++; ?></td>
										<td class="text-center"><?= filter_output($rowPaslon['judul_paslon']); ?></td>
										<td class="text-center"><?= filter_output($rowPaslon['ketua_paslon']); ?></td>
										<td class="text-center"><?= filter_output($rowPaslon['wakil_paslon']); ?></td>
									</tr>
								<?php endforeach; ?>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="col-8 col-md-8 col-sm-12 col-xs-12">
				<div class="card card-dark">
					<div class="card-header">
						<h3 class="card-title">Calon Pemilih</h3>
							<span data-toggle="modal" id="tombolKunci" data-target="#modalKunci" >
								<a href="#" class="btn btn-primary float-right" role="button" ><i class="fas fa-plus-square"></i>   &nbspTambah</a>
							</span>
					</div>
					<div class="card-body">
						<table id="tabelPemilih" class="table table-bordered table-hover" style="width: 100%">
							<thead>
							<tr>
								<th class="text-center">No</th>
								<th class="text-center">Nama Pemilih</th>
								<th class="text-center">Action</th>
							</tr>
							</thead>
							<tbody>
							<?php $no=1;foreach($dataPemilih as $rowPemilih) :?>
								<tr>
									<td class="text-center"><?= filter_output($no++); ?></td>
									<td class="text-center"><?= filter_output($rowPemilih['nama']); ?></td>
									<td class="text-center"><button class="btn btn-danger" data-toggle="tooltip" data-placement="top" title="Hilangkan Hak Akses"><i class="fas fa-window-close"></i></button> </td>
								</tr>
							<?php endforeach; ?>
							</tbody>
						</table>
					</div>
				</div>
				<!-- /.card -->
			</div>
		</div>
		<!-- Default box -->


	</section>
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->

<!--	Modal Kunci Pesan	-->
<div class="modal fade" id="modalKunci" tabindex="-1" aria-labelledby="modalHapusLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="exampleModalLabel">Beri Hak Akses</h5>
			</div>
			<div class="modal-body">
				<table id="tabelCalon" class="table table-bordered table-hover" style="width: 100%">
					<thead>
					<tr>
						<th class="text-center">No</th>
						<th class="text-center">Nama Pemilih</th>
						<th class="text-center">Action</th>
					</tr>
					</thead>
					<tbody>
					<?php $no=1;foreach($dataCalonPemilih as $rowPemilih) :?>
						<tr>
							<td class="text-center"><?= filter_output($no++); ?></td>
							<td class="text-center"><?= filter_output($rowPemilih['nama']); ?></td>
							<td class="text-center">
								<a href="<?= base_url('Kategori/tambah_pemilih/'.$idKategori.'/'.filter_output($rowPemilih['id_user'])); ?>"><button class="btn btn-success" data-toggle="tooltip" data-placement="top" title="Tambahkan Peserta"><i class="fas fa-plus-square"></i></button></a>
							</td>
						</tr>
					<?php endforeach; ?>
					</tbody>
				</table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Batal</button>
			</div>
		</div>
	</div>
</div>
<!--	/Modal Kunci Pesan	-->
