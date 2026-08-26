/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
let auth = ''
export function setCredentials(username,password){auth='Basic '+btoa(`${username}:${password}`)}
export async function request(path,options={}){
  const response=await fetch(path,{...options,headers:{'Content-Type':'application/json','Authorization':auth,...options.headers}})
  const body=await response.json().catch(()=>({message:'服务响应异常'}))
  if(!response.ok||body.success===false)throw new Error(body.message||`请求失败 ${response.status}`)
  return body.data
}
export const api={
  catalog:()=>request('/api/catalog'),dashboard:()=>request('/api/dashboard'),records:(module='')=>request('/api/records'+(module?`?module=${module}`:'')),
  create:data=>request('/api/records',{method:'POST',body:JSON.stringify(data)}),update:(id,data)=>request(`/api/records/${id}`,{method:'PUT',body:JSON.stringify(data)}),
  remove:id=>request(`/api/records/${id}`,{method:'DELETE'}),action:(id,action,remark)=>request(`/api/records/${id}/actions`,{method:'POST',body:JSON.stringify({action,remark})}),
  audits:()=>request('/api/admin/audit-logs'),settings:()=>request('/api/admin/settings'),saveSettings:data=>request('/api/admin/settings',{method:'PUT',body:JSON.stringify(data)}),
  enterpriseControls:(state='')=>request('/api/enterprise/controls'+(state?`?state=${state}`:'')),
  enterpriseSummary:()=>request('/api/enterprise/summary'),
  createEnterprise:data=>request('/api/enterprise/controls',{method:'POST',body:JSON.stringify(data)}),
  submitEnterprise:id=>request(`/api/enterprise/controls/${id}/submit`,{method:'POST'}),
  reviewEnterprise:(id,decision)=>request(`/api/admin/enterprise/controls/${id}/review`,{method:'POST',body:JSON.stringify({decision,remark:'工作台复核'})}),
  registerEnterpriseDocument:id=>request(`/api/enterprise/controls/${id}/documents`,{method:'POST',body:JSON.stringify({fileName:'业务凭证.pdf',mediaType:'application/pdf',sizeBytes:2048,sha256:'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa',storageKey:`enterprise/${id}/evidence.pdf`})}),
  completeEnterprise:id=>request(`/api/enterprise/controls/${id}/complete`,{method:'POST'}),
  syncEnterprise:id=>request(`/api/admin/enterprise/controls/${id}/sync`,{method:'POST',body:JSON.stringify({success:true,externalRef:`ADAPTER-${id}`,message:'工作台模拟适配器回执'})})
}
