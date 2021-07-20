<?php
defined('BASEPATH') or exit('No direct script access allowed');?>
<!-- Content Wrapper. Contains page content -->
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<div class="container-fluid">
			<div class="row mb-2">
				<div class="col-sm-6">
					<h1>Daftar Pemilih</h1>
				</div>
				<div class="col-sm-6">
					<ol class="breadcrumb float-sm-right">
						<li class="breadcrumb-item"><a href="<?= base_url('Member'); ?>">Home</a></li>
						<li class="breadcrumb-item active">Pemilih</li>
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
				<h3 class="card-title">Daftar Calon Pemilih</h3>

			</div>
			<div class="card-body">
				<table id="tabelKategori" class="table table-bordered table-hover">
					<thead>
					<tr>
						<th class="text-center">No</th>
						<th class="text-center">Nama Lengkap</th>
						<th class="text-center">Email</th>
						<th class="text-center">No.Identitas</th>
						<th class="text-center">Status</th>
						<th class="text-center">Action</th>
					</tr>
					</thead>
					<tbody>
					<?php
					$no = 1;
					foreach ($dataUser as $rowUser) :
						$status = filter_output($rowUser['status']);
						?>
						<tr>
							<td class="text-center"><?= filter_output($no++); ?></td>
							<td class="text-center"><?= filter_output($rowUser['nama']); ?></td>
							<td class="text-center"><?= filter_output($rowUser['email']); ?></td>
							<td class="text-center"><?= filter_output($rowUser['identitas']); ?></td>
							<td class="text-center"><?= ($status == 2) ? "<small class=\"badge badge-warning\"> BELUM AKTIF </small>": "<small class=\"badge badge-primary\"> AKTIF </small>"; ?></td>
							<td class="text-center">
								<?php if($status == 2) {?>
									<a href="<?= base_url('User/aktivasi/'.filter_output($rowUser['id_user'])); ?>"><button class="btn btn-success"><i class="fas fa-check-square"></i></button></a>
								<?php } ?>
								<a href="<?= base_url('User/hapus/'.filter_output($rowUser['id_user'])); ?>"><button class="btn btn-danger"><i class="fas fa-trash"></i></button></a>
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
