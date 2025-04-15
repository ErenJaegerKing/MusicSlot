import request from '@/utils/request'
// 上传校验
export function checkUpload(MD5) {
  return request({
    url: `upload/multipart/check?md5=${MD5}`,
    method: 'get',
  })
};

// 初始化上传
export function initUpload(data) {
  return request({
    url: `upload/multipart/init`,
    method: 'post',
    data
  })
};
