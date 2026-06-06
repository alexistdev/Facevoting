<?php
defined('BASEPATH') or exit('No direct script access allowed');?>
<!-- Content Wrapper. Contains page content -->
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<div class="container-fluid">
			<div class="row mb-2">
				<div class="col-sm-6">
					<h1>Daftar Paslon</h1>
				</div>
				<div class="col-sm-6">
					<ol class="breadcrumb float-sm-right">
						<li class="breadcrumb-item"><a href="<?= base_url('Member'); ?>">Home</a></li>
						<li class="breadcrumb-item active">Daftar Paslon</li>
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
				<h3 class="card-title">Daftar Paslon</h3>
				<a href="<?= base_url('Paslon/tambah'); ?>"><button class="btn btn-sm btn-primary float-right">Tambah</button></a>
			</div>
			<div class="card-body">
				<table id="tabelPaslon" class="table table-bordered table-hover" style="width: 100%;">
					<thead>
					<tr>
						<th class="text-center">No</th>
						<th class="text-center">Kategori</th>
						<th class="text-center">Nomor Urut</th>
						<th class="text-center">Calon Ketua</th>
						<th class="text-center">Calon Wakil</th>
						<th class="text-center" width="10%">Visi dan Misi</th>
						<th class="text-center" width="15%">Profil Catum</th>
						<th class="text-center" width="15%">Profil Cawatum</th>
						<th class="text-center" width="10%">Action</th>
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
							<td class="text-center"><?= filter_output($rowPaslon['visi_misi']); ?></td>
							<td class="text-center"><?= filter_output($rowPaslon['profil_catum']); ?></td>
							<td class="text-center"><?= filter_output($rowPaslon['profil_cawatum']); ?></td>
							<td class="text-center">
								<span data-toggle="modal" id="tombolHapus" data-target="#modalHapus" data-id="<?= filter_output($rowPaslon['id_paslon']); ?>">
									<button class="btn btn-danger"><i class="fas fa-trash"></i></button>
								</span>
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
<!--	Modal Kunci Pesan	-->
<div class="modal fade" id="modalHapus" tabindex="-1" aria-labelledby="modalHapusLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="exampleModalLabel">Hapus Data</h5>
			</div>
			<div class="modal-body">
				Apakah anda yakin ingin menghapus data ini ?
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Batal</button>
				<a href="" id="urlKunci"><button  type="button" class="btn btn-danger">Hapus</button></a>
			</div>
		</div>
	</div>
</div>
<!--	/Modal Kunci Pesan	-->
