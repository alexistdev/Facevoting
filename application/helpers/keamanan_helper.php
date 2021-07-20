<?php
defined('BASEPATH') or exit('No direct script access allowed');
function filter_output($str)
{
	return htmlentities($str, ENT_QUOTES, 'UTF-8');
}
