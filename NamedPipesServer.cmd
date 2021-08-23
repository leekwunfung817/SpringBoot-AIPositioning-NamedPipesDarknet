cd %~dp0
cd darknet_resources
set NamedPipesDarknet=../darknet_release/darknet.exe
"%NamedPipesDarknet%" detector ^
 test_detector_named_pipes_service ^
 ctr.config.data ^
 ctr.cfg ^
 ctr.weights ^
 -thresh 0.01 ^
 -dont_show -save_labels ^
 -ext_output 11_1_i_h__20190621093202_27871908.jpg

cd %~dp0