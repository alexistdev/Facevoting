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
					<h1>Tambah Pasangan Calon</h1>
				</div>
				<div class="col-sm-6">
					<ol class="breadcrumb float-sm-right">
						<li class="breadcrumb-item"><a href="<?= base_url('Member') ?>">Home</a></li>
						<li class="breadcrumb-item active">Tambah Paslon</li>
					</ol>
				</div>
			</div>
		</div><!-- /.container-fluid -->
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-12">
					<?php
					echo $this->session->flashdata('message1');
					echo $this->session->flashdata('message2'); ?>
				</div>
			</div>
			<form action="<?= base_url('Paslon/tambah'); ?>" method="post" enctype="multipart/form-data">
				<div class="row">
					<!--	Form Ruas Kiri		-->
					<div class="col-md-6">
						<div class="card">
							<div class="card-body">
								<!-- Pilihan kategori -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="namaKategori">Nama Kategori <span class="text-danger">*</span></label>
											<select name="namaKategori" class="form-control" required="required">
												<option value="">Pilih</option>
												<?php foreach($daftarKategori->result_array() as $rowKategori): ?>
													<option value="<?= filter_output($rowKategori['id_kategori']); ?>"><?= filter_output($rowKategori['nama_kategori']); ?></option>
												<?php endforeach; ?>
											</select>
										</div>
									</div>
								</div>
								<!-- Judul Paslon -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="judulPaslon">Judul Paslon <span class="text-danger">*</span></label>
											<input type="text" name="judulPaslon" id="judulPaslon" maxlength="20" class="form-control" value="<?= set_value('judulPaslon'); ?>" placeholder="Judul Paslon">
										</div>
									</div>
								</div>
								<!-- Ketua Paslon -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="ketuaPaslon">Nama Calon Ketua <span class="text-danger">*</span></label>
											<input type="text" name="ketuaPaslon" id="ketuaPaslon" maxlength="50" class="form-control" value="<?= set_value('ketuaPaslon'); ?>" placeholder="Nama Calon Ketua">
										</div>
									</div>
								</div>
								<!-- Profil Catum -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="profilCatum">Profil Calon Ketua <span class="text-danger">*</span></label>
											<textarea name="profilCatum" id="profilCatum" class="form-control" cols="10" rows="5" maxlength="200" required="required"><?= set_value('profilCatum'); ?></textarea>
										</div>
									</div>
								</div>
								<!-- Wakil Paslon -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="wakilPaslon">Nama Calon Ketua <span class="text-danger">*</span></label>
											<input type="text" name="wakilPaslon" id="wakilPaslon" maxlength="50" class="form-control" value="<?= set_value('wakilPaslon'); ?>" placeholder="Nama Calon Wakil">
										</div>
									</div>
								</div>
								<!-- Profil Catum -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="profilCawatum">Profil Calon Wakil <span class="text-danger">*</span></label>
											<textarea name="profilCawatum" id="profilCawatum" class="form-control" cols="10" rows="5" maxlength="200" required="required"><?= set_value('profilCawatum'); ?></textarea>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<!--	Form Ruas Kanan		-->
					<div class="col-md-6">
						<div class="card">
							<div class="card-body">
								<!-- Visi_misi -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="visimisi">Visi dan Misi <span class="text-danger">*</span></label>
											<textarea name="visimisi" id="visimisi" class="form-control" cols="10" rows="5" maxlength="400" required="required"></textarea>
										</div>
									</div>
								</div>
								<!-- Photo Ketua -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="photo1">Photo Ketua <span class="text-danger">*</span></label><br>
											<input type="file" id="photo1" name="photo1" required="required"/>
											<br>
											<span class="text-danger">** Upload Gambar Format JPG/PNG/GIF Maksimal 2 Mb.</span>
										</div>
									</div>
								</div>
								<!-- Photo Ketua -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="photo2">Photo Wakil <span class="text-danger">*</span></label><br>
											<input type="file" id="photo2" name="photo2" required="required"/>
											<br>
											<span class="text-danger">** Upload Gambar Format JPG/PNG/GIF Maksimal 2 Mb.</span>
										</div>
									</div>
								</div>
								<!-- Submit -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<button type="submit" class="btn btn-primary">Tambah</button>
											<a href="<?= base_url('Paslon'); ?>"><button type="button" class="btn btn-danger">Batal</button></a>
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
