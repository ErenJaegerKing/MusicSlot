import request from '@/utils/request'

// 上传校验
export function checkUpload(MD5) {
  return request({
    url: `upload/multipart/check?md5=${MD5}`,
    method: 'get',
  }, {
    // 如果用的是 axios，可以加这个选项禁止自动处理响应
    transformResponse: [data => data]
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

// 合并上传
export function mergeUpload(data) {
  return request({
    url: `upload/multipart/merge`,
    method: 'post',
    data
  })
};
