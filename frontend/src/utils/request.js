// 请求工具类，用于自动携带令牌

/**
 * 发送带令牌的请求
 * @param {string} url - 请求地址
 * @param {Object} options - 请求选项
 * @returns {Promise} - 返回Promise对象
 */
export async function request(url, options = {}) {
  // 从localStorage获取令牌
  const token = localStorage.getItem('token');
  
  // 设置默认请求头
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers
  };
  
  // 如果有令牌，添加到请求头
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  // 发送请求
  const response = await fetch(url, {
    ...options,
    headers
  });
  
  // 处理响应
  if (!response.ok) {
    // 如果是401未授权，清除令牌并跳转到登录页
    if (response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    
    // 抛出错误
    const error = await response.text();
    throw new Error(error);
  }
  
  // 返回响应数据
  return response.json();
}

/**
 * 发送GET请求
 * @param {string} url - 请求地址
 * @param {Object} options - 请求选项
 * @returns {Promise} - 返回Promise对象
 */
export function get(url, options = {}) {
  return request(url, {
    ...options,
    method: 'GET'
  });
}

/**
 * 发送POST请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求数据
 * @param {Object} options - 请求选项
 * @returns {Promise} - 返回Promise对象
 */
export function post(url, data, options = {}) {
  return request(url, {
    ...options,
    method: 'POST',
    body: JSON.stringify(data)
  });
}

/**
 * 发送PUT请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求数据
 * @param {Object} options - 请求选项
 * @returns {Promise} - 返回Promise对象
 */
export function put(url, data, options = {}) {
  return request(url, {
    ...options,
    method: 'PUT',
    body: JSON.stringify(data)
  });
}

/**
 * 发送DELETE请求
 * @param {string} url - 请求地址
 * @param {Object} options - 请求选项
 * @returns {Promise} - 返回Promise对象
 */
export function del(url, options = {}) {
  return request(url, {
    ...options,
    method: 'DELETE'
  });
}
