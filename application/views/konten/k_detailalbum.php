<?php
defined('BASEPATH') or exit('No direct script access allowed');?>
<!-- Content Wrapper. Contains page content -->
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<div class="container-fluid">
			<div class="row mb-2">
				<div class="col-sm-6">
					<h1>View Response Album</h1>
				</div>
				<div class="col-sm-6">
					<ol class="breadcrumb float-sm-right">
						<li class="breadcrumb-item"><a href="<?= base_url('Member'); ?>">Home</a></li>
						<li class="breadcrumb-item">Album</li>
						<li class="breadcrumb-item active">View Response Album</li>
					</ol>
				</div>
			</div>
		</div><!-- /.container-fluid -->
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">

			<!-- Default box -->
			<div class="card card-dark">
				<div class="card-header">
					<h3 class="card-title">Response Album dari Lambda</h3>

				</div>
				<div class="card-body">
					<?= $getViewAlbum; ?>


				</div>
		</div>
		<!-- /.card -->
		</div>
	</section>
	<!-- /.content -->
</div>
<!-- /.content-wrapper -->
