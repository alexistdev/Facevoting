<?php
defined('BASEPATH') OR exit('No direct script access allowed');
/**
 * FaceVoting REST API
 * Author: Berkat Fa'atulo Halawa
 */
class User extends CI_Controller
{

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
			$data['dataUser']= $this->admin->get_data_user(null)->result_array();
			$view ='v_user';
			$this->_template($data,$view);
		}

	}

	/** Method untuk aktivasi user dan mendapatkan data album*/
	public function aktivasi($idx=null){
		$id = $idx;
		if($idx == null || $idx == ''){
			redirect('User');
		} else {
			/* Mengecek bahwa sudah aktif atau tidak */
			$getUser = $this->admin->get_data_user($id);
			if($getUser->num_rows() == 0){
				redirect('User');
			} else {
				$dataStatus = [
					'status' => 1
				];
				/* Mengupdate data status */
				$this->admin->aktivasi_user($dataStatus,$id);
				$this->session->set_flashdata('pesan2', '<div class="alert alert-success" role="alert">User telah berhasil diaktifkan!</div>');
				redirect('User');
			}
		}
	}

	/** Method untuk aktivasi user dan mendapatkan data album*/
	public function hapus($idx=null){
		$id = $idx;
		if($idx == null || $idx == ''){
			redirect('User');
		} else {
			$getUser = $this->admin->get_data_user2($id);
			if($getUser->num_rows() == 0){
				redirect('User');
			} else {
				$this->admin->hapus_user($id);
				$this->session->set_flashdata('pesan', '<div class="alert alert-danger" role="alert">User telah dihapus!</div>');
				redirect('User');
			}
		}
	}




}
