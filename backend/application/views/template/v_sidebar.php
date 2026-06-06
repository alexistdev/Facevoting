<?php
defined('BASEPATH') or exit('No direct script access allowed');?>
<!-- Main Sidebar Container -->
<aside class="main-sidebar sidebar-dark-primary elevation-4">
	<!-- Brand Logo -->
	<a href="<?= base_url('Member'); ?>" class="brand-link">
		<img src="<?= base_url('gambar/logomurni.png'); ?>"
			 alt="AdminLTE Logo"
			 class="brand-image img-circle elevation-3"
			 style="opacity: .8">
		<span class="brand-text font-weight-light">FaceVoting</span>
	</a>

	<!-- Sidebar -->
	<div class="sidebar">
		<!-- Sidebar user (optional) -->
		<div class="user-panel mt-3 pb-3 mb-3 d-flex">
			<div class="image">
				<img src="<?= base_url('gambar/default.jpg'); ?>" class="img-circle elevation-2" alt="User Image">
			</div>
			<div class="info">
				<a href="#" class="d-block">Dashboard</a>
			</div>
		</div>

		<!-- Sidebar Menu -->
		<nav class="mt-2">
			<ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">
				<!-- Add icons to the links using the .nav-icon class
					 with font-awesome or any other icon font library -->
<!--				<li class="nav-item has-treeview">-->
<!--					<a href="--><!--" class="nav-link">-->
<!--						<i class="nav-icon fas fa-tachometer-alt"></i>-->
<!--						<p>-->
<!--							Dashboard-->
<!--						</p>-->
<!--					</a>-->
<!--				</li>-->
				<li class="nav-item has-treeview">
					<a href="#" class="nav-link">
						<i class="nav-icon fas fa-th"></i>
						<p>
							Pemilihan Umum
							<i class="right fas fa-angle-left"></i>
						</p>
					</a>
					<ul class="nav nav-treeview">
						<li class="nav-item">
							<a href="<?= base_url('Kategori'); ?>" class="nav-link">
								<i class="far fa-circle nav-icon"></i>
								<p>Jenis Pemilihan</p>
							</a>
						</li>
						<li class="nav-item">
							<a href="<?= base_url('Paslon'); ?>" class="nav-link">
								<i class="far fa-circle nav-icon"></i>
								<p>Pasangan Calon</p>
							</a>
						</li>
					</ul>
				</li>
				<li class="nav-item">
					<a href="<?= base_url('Hasil'); ?>" class="nav-link">
						<i class="nav-icon fas fa-chart-bar"></i>
						<p>
							Hasil
						</p>
					</a>
				</li>
				<li class="nav-item">
					<a href="<?= base_url('User'); ?>" class="nav-link">
						<i class="nav-icon fas fa-user"></i>
						<p>
							Pemilih
						</p>
					</a>
				</li>
				<li class="nav-item has-treeview">
					<a href="#" class="nav-link">
						<i class="nav-icon fas fa-portrait"></i>
						<p>
							Database Photo
							<i class="right fas fa-angle-left"></i>
						</p>
					</a>
					<ul class="nav nav-treeview">
						<li class="nav-item">
							<a href="<?= base_url('Photo'); ?>" class="nav-link">
								<i class="far fa-circle nav-icon"></i>
								<p>Photo User</p>
							</a>
						</li>
					</ul>
				</li>
				<li class="nav-item">
					<a href="<?= base_url('Setting'); ?>" class="nav-link">
						<i class="nav-icon fas fa-cog"></i>
						<p>
							Setting
						</p>
					</a>
				</li>
			</ul>
		</nav>
		<!-- /.sidebar-menu -->
	</div>
	<!-- /.sidebar -->
</aside>
