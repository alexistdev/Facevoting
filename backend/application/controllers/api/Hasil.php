<?php
defined('BASEPATH') OR exit('No direct script access allowed');
use chriskacerguis\RestServer\RestController;

class Hasil extends RestController {

	public $api;
	public function __construct()
	{
		parent::__construct();
		$this->load->model('Api_model', 'api');
	}




}
