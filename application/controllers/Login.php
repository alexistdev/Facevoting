<?php
defined('BASEPATH') OR exit('No direct script access allowed');
/**
 * FaceVoting REST API
 * Author: Berkat Fa'atulo Halawa
 */
class Login extends CI_Controller {

	public $input;
	public $load;
	public $session;
	public $form_validation;
	public $login;

	/** Constructor dari Class Login */
	public function __construct()
	{
		parent::__construct();
		$this->load->model('admin_m', 'login');
		if ($this->session->userdata('is_login_in') == TRUE) {
			redirect('Member');
		}
	}

	/** Template untuk memanggil view */
	private function _template($data, $view)
	{
		$this->load->view('view/' . $view, $data);
	}

	/** Method untuk generate captcha */
	private function _create_captcha()
	{
		$cap = create_captcha(config_captcha());
		$image = $cap['image'];
		$this->session->set_userdata('captchaword', $cap['word']);
		return $image;
	}

	/** Method untuk memvalidasi apakah captcha yang dimasukkan sudah benar */
	public function _check_captcha($string)
	{
		if ($string == $this->session->userdata('captchaword')) {
			return TRUE;
		} else {
			$this->form_validation->set_message('_check_captcha', 'Captcha yang anda masukkan salah!');
			return FALSE;
		}
	}


	/** Menampilkan halaman default login, dengan form validation */
	public function index()
	{
		$this->form_validation->set_rules(
			'username',
			'Username',
			'trim|required',
			[
				'required' => 'Username harus diisi!'
			]
		);
		$this->form_validation->set_rules(
			'password',
			'Password',
			'trim|required',
			[
				'required' => 'Password harus diisi!'
			]
		);
		$this->form_validation->set_rules(
			'captcha',
			'Captcha',
			'trim|callback__check_captcha|required',
			[
				'required' => 'Captcha harus diisi!'
			]
		);
		$this->form_validation->set_error_delimiters('<div class="alert alert-danger" role="alert">', '</div>');
		if ($this->form_validation->run() === false) {
			$this->session->set_flashdata('pesan', validation_errors());
			$data['image'] = $this->_create_captcha();
			$data['title'] = "Login | FaceVoting Versi 1.0";
			$view ='v_login';
			$this->_template($data,$view);
		} else {
			$username = $this->input->post('username', TRUE);
			$password = $this->input->post('password', TRUE);
			$cekLogin = $this->login->validasi_login($username)->row();
			if(!password_verify($password, $cekLogin->password)){
				$this->session->set_flashdata('pesan2', '<div class="alert alert-danger" role="alert">Username atau password anda salah!</div>');
				redirect("Login");
			} else {
				$waktu = date('Y-m-d H:i:s');
				$key = sha1($waktu);
				$logTime = strtotime($waktu);

				/* Cek apa token sudah ada apa belum, jika ada dihapus */
				$cekToken = $this->login->cek_token($username);
				if($cekToken > 0){
					//jalankan hapus token
					$this->login->hapus_token(1);
				}

				/* mempersiapkan data untuk session */
				$data_session = [
					'id_admin' => 1,
					'token' => $key,
					'is_login_in' => TRUE
				];

				/* mempersiapkan data untuk token */
				$hashkey = [
					'id_admin' => 1,
					'token' => $key,
					'time' => $logTime
				];
				/* Menyimpan data token */
				$this->login->simpan_token($hashkey);
				/* Mengeset data session */
				$this->session->set_userdata($data_session);
				redirect("Member");
			}
		}
	}
}
