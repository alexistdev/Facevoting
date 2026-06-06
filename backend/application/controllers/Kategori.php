<?php
defined('BASEPATH') OR exit('No direct script access allowed');
/**
 * FaceVoting REST API
 * Author: Berkat Fa'atulo Halawa
 */
class Kategori extends CI_Controller {

	public $input;
	public $load;
	public $session;
	public $form_validation;
	public $admin;
	public $tokenSession;
	public $tokenServer;
	public $upload;

	/** Constructor dari Class Login */
	public function __construct()
	{
		parent::__construct();
		$this->load->model('Admin_m', 'admin');
		if ($this->session->userdata('is_login_in') !== TRUE) {
			redirect('login');
		}
		$this->idUser = $this->session->userdata('id_admin');
		$this->tokenSession = $this->session->userdata('token');
		$this->tokenServer = $this->admin->get_token_byId($this->idUser)->row()->token;

	}

	/** Template untuk memanggil view */
	private function _template($data, $view)
	{
		$this->load->view('view/' . $view, $data);
	}

	/** Menampilkan halaman index Member */
	public function index()

	{
		if($this->tokenSession != $this->tokenServer){
			_unlogin();
		} else {
			$data['title'] = "Dashboard | FaceVoting Versi 1.0";
			$data['dataKategori']= $this->admin->get_data_kategori()->result_array();
			$view ='v_kategori';
			$this->_template($data,$view);
		}
	}
	/** Method untuk halaman detail */
	public function detail($idx=NULL)
	{
		if($this->tokenSession != $this->tokenServer){
			_unlogin();
		} else {
			$id = $idx;
			$cekid = $this->admin->get_data_kategori($id);

			if($idx == NULL || $idx = '' || $cekid->num_rows() == 0){
				redirect('Kategori');
			} else {
				$data['title'] = "Dashboard | FaceVoting Versi 1.0";
				$data['dataPaslon']= $this->admin->get_data_paslon_byKategori($id)->result_array();
				$data['dataPemilih']= $this->admin->get_data_pemilih($id)->result_array();
				$data['dataCalonPemilih'] = $this->admin->get_data_calon($id)->result_array();
				$data['idKategori'] = $id;
				$view ='v_detailkategori';
				$this->_template($data,$view);

			}
		}
	}

	public function tambah_pemilih($idKat=NULL,$idUser=NULL){
		if($this->tokenSession != $this->tokenServer){
			_unlogin();
		} else {
			if($idKat==NULL || $idKat=='' || $idUser==NULL || $idUser==''){
				redirect('Kategori');
			}else{
				$idKategori = $idKat;
				$idS = $idUser;
				$cekKategori = $this->admin->get_data_kategori($idKategori);
				$cekUser = $this->admin->get_data_user2($idS);
				if($cekKategori->num_rows() == 0 || $cekUser->num_rows() == 0){
					redirect('Kategori');
				} else {
					$dataOtorisasi =
						[
							'id_user' => $idS,
							'id_kategori' => $idKategori,
							'status' => 1
						];
					$this->admin->simpan_otorisasi($dataOtorisasi);
					$this->session->set_flashdata('message', '<div class="alert alert-success" role="alert">User berhasil diberi hak akses!</div>');
					redirect('Kategori/detail/'.$idKategori);
				}
			}
		}
	}

	public function tambah()
	{
		if($this->tokenSession != $this->tokenServer){
			_unlogin();
		} else {
			$this->form_validation->set_rules(
				'namaKategori',
				'Nama Kategori',
				'trim|required',
				[
					'required' => 'Nama Kategori harus diisi!'
				]
			);
			$this->form_validation->set_error_delimiters('<div class="alert alert-danger" role="alert">', '</div>');
			if ($this->form_validation->run() === false) {
				$this->session->set_flashdata('message1', validation_errors());
				$data['title'] = "Dashboard | FaceVoting Versi 1.0";
				$view = 'v_tambahkategori';
				$this->_template($data, $view);
			} else {
				$namaKategori = $this->input->post("namaKategori", TRUE);
				$namaFile = substr(sha1(time()), 0, 10);
				/* Upload gambar */
				$config['upload_path'] = './gambar/';
				$config['allowed_types'] = 'jpg|jpeg|png|gif';
				$config['max_size'] = 2424;
				$config['file_name'] = $namaFile;
				$this->load->library('upload', $config);
				if (!$this->upload->do_upload('logo')) {
					$error = array('error' => $this->upload->display_errors());
					$this->session->set_flashdata('message2', '<div class="alert alert-danger" role="alert">' . $error['error'] . '</div>');
					redirect('Kategori/tambah');
				} else {
					$dataKategori = [
						'nama_kategori' => $namaKategori,
						'logo_kategori' => $this->upload->data('file_name'),
						'status_kategori' => 1
					];
					$this->admin->simpan_kategori($dataKategori);
					$this->session->set_flashdata('message2', '<div class="alert alert-success" role="alert">Kategori berhasil ditambahkan!</div>');
					redirect("Kategori");
				}
			}
		}
	}

	/** Bagian untuk menutup Pemilu */
	public function Tutup($idx=null)
	{
		$id = $idx;
		if ($this->tokenSession != $this->tokenServer) {
			_unlogin();
		} else {
			if(!empty($idx)){
				$getData = $this->admin->get_data_kategori($id);
				if($getData->num_rows() == 0){
					redirect("Kategori");
				}else{
					$dataKategori = [
						'status_kategori' => 1
					];
					$this->admin->update_kategori($dataKategori,$id);
					$this->session->set_flashdata('message2', '<div class="alert alert-danger" role="alert">Pemilu saat ini telah ditutup!</div>');
					redirect("Kategori");
				}
			} else {
				redirect("Kategori");
			}
		}
	}

	/** Bagian untuk membuka Pemilu */
	public function Buka($idx=null)
	{
		$id = $idx;
		if ($this->tokenSession != $this->tokenServer) {
			_unlogin();
		} else {
			if (!empty($idx)) {
				$getData = $this->admin->get_data_kategori($id);
				if ($getData->num_rows() == 0) {
					redirect("Kategori");
				} else {
					$dataKategori = [
						'status_kategori' => 2
					];
					$this->admin->update_kategori($dataKategori, $id);
					$this->session->set_flashdata('message2', '<div class="alert alert-success" role="alert">Pemilu saat ini telah dibuka!</div>');
					redirect("Kategori");
				}
			}
		}
	}
}
