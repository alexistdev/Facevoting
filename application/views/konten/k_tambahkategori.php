<?php
defined('BASEPATH') or exit('No direct script access allowed');
?>
<!-- Content Wrapper. Contains page content -->
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<div class="container-fluid">
			<div class="row mb-2">
				<div class="col-sm-6">
					<h1>Tambah Kategori</h1>
				</div>
				<div class="col-sm-6">
					<ol class="breadcrumb float-sm-right">
						<li class="breadcrumb-item"><a href="<?= base_url('Member') ?>">Home</a></li>
						<li class="breadcrumb-item active">Tambah Kategori</li>
					</ol>
				</div>
			</div>
		</div><!-- /.container-fluid -->
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-6">
					<?php
					echo $this->session->flashdata('message1');
					echo $this->session->flashdata('message2'); ?>
				</div>
			</div>
			<form action="<?= base_url('Kategori/tambah'); ?>" method="post" enctype="multipart/form-data">
				<div class="row">
					<!--	Form Ruas Kiri		-->
					<div class="col-md-6">
						<div class="card">
							<div class="card-body">
								<!-- Nama Kategori -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="namaKategori">Nama Kategori <span class="text-danger">*</span></label>
											<input type="text" name="namaKategori" id="namaKategori" maxlength="80" class="form-control" value="<?= set_value('namaKategori'); ?>" placeholder="Nama Kategori">
										</div>
									</div>
								</div>
								<!-- Logo -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<input type="file" id="logo" name="logo" required="required"/>
											<br>
											<span class="text-danger">** Upload Gambar Format JPG/PNG/GIF Maksimal 2 Mb.</span>
										</div>
									</div>
								</div>
								<!-- Submit -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<button type="submit" class="btn btn-primary">Simpan</button>
											<a href="<?= base_url('Kategori'); ?>"><button type="button" class="btn btn-danger">Batal</button></a>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>

				</div>
			</form>
		</div>
	</section>
</div>
<!-- /.content-wrapper -->
