document.getElementById('search-id-btn').addEventListener('click',async () => {

  const inputEmail = document.getElementById('searchId').value;

  console.log('입력한 이메일:', inputEmail);
  try{
    const response = await ajax.post('/api/search-info/search-id',{
      email: inputEmail
    });
    console.log('서버 응답:', response);
    document.getElementById('message').innerText = response.message;
  } catch (error) {
    console.error('서버 응답 처리중 오류가 발생했습니다.')
    document.getElementById('message').innerText = '서버 응답을 처리하는 중 오류가 발생했습니다.';
  }
});

document.getElementById('search-pw-btn').addEventListener('click',async () => {

  const inputId = document.getElementById('findPwId').value;
  const inputEmail = document.getElementById('findPwEmail').value;

  console.log('입력한 아이디:', inputId);
  console.log('입력한 이메일:', inputEmail);

  try {
    const response = await ajax.post('/api/search-info/search-pw', {
      email: inputEmail,
      memberId: inputId
    });
    console.log('서버 응답:', response);
    document.getElementById('send-pw-message').innerText = response.message;
  } catch (error) {
    console.error('서버 응답 처리중 오류가 발생했습니다.')
    document.getElementById('send-pw-message').innerText = '서버 응답을 처리하는 중 오류가 발생했습니다.';
  }
});

