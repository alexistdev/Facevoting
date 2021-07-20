<?php
defined('BASEPATH') OR exit('No direct script access allowed');
/**
 * FaceVoting REST API
 * Author: Berkat Fa'atulo Halawa
 */
class Member extends CI_Controller {

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
			$view ='v_member';
			$this->_template($data,$view);
		}

	}

	/** Method untuk Logout */
	public function logout()
	{
		$this->session->sess_destroy();
		redirect('Login');
	}
}
