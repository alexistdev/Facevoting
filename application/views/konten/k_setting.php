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
					<h1>Setting</h1>
				</div>
				<div class="col-sm-6">
					<ol class="breadcrumb float-sm-right">
						<li class="breadcrumb-item"><a href="<?= base_url('Member') ?>">Home</a></li>
						<li class="breadcrumb-item active">Setting</li>
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
			<form action="<?= base_url('Setting'); ?>" method="post">
				<div class="row">
					<!--	Form Ruas Kiri		-->
					<div class="col-md-6">
						<div class="card">
							<div class="card-body">
								<!-- Kode Jurusan -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="password1">Password <span class="text-danger">*</span></label>
											<input type="password" name="password1" id="password1" maxlength="16" class="form-control" value="<?= set_value('password1'); ?>" placeholder="******" required="required">
										</div>
									</div>
								</div>
								<!-- Nama Jurusan -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="password2">Ulangi Password<span class="text-danger">*</span></label>
											<input type="password" name="password2" id="password2" maxlength="16" class="form-control" value="<?= set_value('password2'); ?>" placeholder="******" required="required">
										</div>
									</div>
								</div>
								<!-- Submit -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<button type="submit" class="btn btn-primary">Simpan</button>
											<a href="<?= base_url('Member'); ?>"><button type="button" class="btn btn-danger">Batal</button></a>
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
