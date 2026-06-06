<?php
defined('BASEPATH') OR exit('No direct script access allowed');
/**
 * FaceVoting REST API
 * Author: Berkat Fa'atulo Halawa
 */
class Paslon extends CI_Controller {

	public $input;
	public $load;
	public $session;
	public $form_validation;
	public $admin;
	public $tokenSession;
	public $tokenServer;

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
			$data['dataPaslon']= $this->admin->get_data_paslon()->result_array();
			$view ='v_paslon';
			$this->_template($data,$view);
		}
	}

	public function hapus($id=null){
		if ($this->tokenSession != $this->tokenServer) {
			_unlogin();
		} else {
			if(!empty($id)){
				$getData = $this->admin->cek_paslon($id);
				if($getData->num_rows() == 0){
					redirect("Paslon");
				}else {
					$this->admin->hapus_paslon($id);
					$this->session->set_flashdata('message2', '<div class="alert alert-danger" role="alert">Data paslon berhasil dihapus!</div>');
					redirect("Paslon");
				}

			} else {
				redirect("Paslon");
			}
		}
	}

	/** Menambah Paslon */
	public function tambah()
	{
		if ($this->tokenSession != $this->tokenServer) {
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
			$this->form_validation->set_rules(
				'judulPaslon',
				'Judul Paslon',
				'trim|required',
				[
					'required' => 'Judul Paslon harus diisi!'
				]
			);
			$this->form_validation->set_rules(
				'ketuaPaslon',
				'Nama Calon Ketua',
				'trim|required',
				[
					'required' => 'Nama Calon Ketua harus diisi!'
				]
			);
			$this->form_validation->set_rules(
				'profilCatum',
				'Profil Calon Ketua',
				'trim|required',
				[
					'required' => 'Profil Calon Ketua harus diisi!'
				]
			);
			$this->form_validation->set_rules(
				'wakilPaslon',
				'Nama Calon Wakil',
				'trim|required',
				[
					'required' => 'Nama Calon Wakil harus diisi!'
				]
			);
			$this->form_validation->set_rules(
				'profilCawatum',
				'Profil Calon Wakil',
				'trim|required',
				[
					'required' => 'Profil Calon Wakil harus diisi!'
				]
			);
			$this->form_validation->set_rules(
				'visimisi',
				'Visi dan Misi',
				'trim|required',
				[
					'required' => 'Visi dan Misi harus diisi!'
				]
			);
			$this->form_validation->set_error_delimiters('<div class="alert alert-danger" role="alert">', '</div>');
			if ($this->form_validation->run() === false) {
				$this->session->set_flashdata('message1', validation_errors());
				$data['title'] = "Dashboard | FaceVoting Versi 1.0";
				$data['daftarKategori'] = $this->admin->get_data_kategori_paslon();
				$view = 'v_tambahpaslon';
				$this->_template($data, $view);
			} else{
				if (isset($_FILES['photo1']) && $_FILES['photo1']['name'] != ''){
					$file1 = $this->ddoo_upload('photo1');
				}

				if (isset($_FILES['photo2']) && $_FILES['photo2']['name'] != ''){
					$file2 = $this->ddoo_upload('photo2');
				}
				$namaKategori = $this->input->post("namaKategori", TRUE);
				$judulPaslon = $this->input->post("judulPaslon", TRUE);
				$ketuaPaslon = $this->input->post("ketuaPaslon", TRUE);
				$profilCatum = $this->input->post("profilCatum", TRUE);
				$wakilPaslon = $this->input->post("wakilPaslon", TRUE);
				$profilCawatum = $this->input->post("profilCawatum", TRUE);
				$visimisi = $this->input->post("visimisi", TRUE);

				$dataPaslon = [
					'id_kategori' => $namaKategori,
					'judul_paslon' => $judulPaslon,
					'ketua_paslon' => $ketuaPaslon,
					'wakil_paslon' => $wakilPaslon,
					'photo1_paslon' => $file1,
					'photo2_paslon' => $file2,
					'perolehan' => 0
				];
				$idPaslon = $this->admin->simpan_paslon($dataPaslon);

				$dataDetailPaslon = [
					'id_paslon' => $idPaslon,
					'visi_misi' => $visimisi,
					'profil_catum' => $profilCatum,
					'profil_cawatum' => $profilCawatum
				];
				$this->admin->simpan_detail_paslon($dataDetailPaslon);
				$this->session->set_flashdata('message2', '<div class="alert alert-success" role="alert">Pasangan Calon berhasil ditambahkan!</div>');
				redirect("Paslon");
			}
		}
	}

	private function ddoo_upload($filename){
		$config['upload_path'] = './gambar/';
		$config['allowed_types'] = 'jpg|jpeg|png|gif';
		$config['max_size'] = 2424;

		$this->load->library('upload', $config);
		if ( ! $this->upload->do_upload($filename)) {
			$error = array('error' => $this->upload->display_errors());
		} else {
			$data_foto = $this->upload->data();
			return $data_foto['file_name'];
		}
	}

}
