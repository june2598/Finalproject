  /*-----------------------------------------------------------------------*
   * client-server간 http api 비동기 통신
   *-----------------------------------------------------------------------*/
  const ajax = {
    get: async url => {                 //function(url) {}
      const option = {
        method: 'GET',
        headers: {
          Accept: 'application/json',
        },
      };
      try {
        const res = await fetch(url, option);
        if(!res.ok){
        throw new Error(`응답오류 : ${res.status}`);
        }
        const json = await res.json();
        return json;                //반환값 : js 객체
      } catch (err) {
        console.error(err.message);
      }
    },
    post: async (url, payload) => {
      const option = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
        body: JSON.stringify(payload), // jsobject => json포맷의 문자열
      };
    try {
           const res = await fetch(url, option);
           if(!res.ok){
           throw new Error(`응답오류 : ${res.status}`);
           }
           const json = await res.json();
           return json;
         } catch (err) {
           console.error(err.message);
         }
    },
    put: async (url, payload) => {
      const option = {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
        body: JSON.stringify(payload),
      };
    try {
           const res = await fetch(url, option);
           if(!res.ok){
           throw new Error(`응답오류 : ${res.status}`);
           }
           const json = await res.json();
           return json;
         } catch (err) {
           console.error(err.message);
         }
    },
    patch: async (url, payload) => {
      const option = {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
        body: JSON.stringify(payload),
      };
      try {
             const res = await fetch(url, option);
             if(!res.ok){
             throw new Error(`응답오류 : ${res.status}`);
             }
             const json = await res.json();
             return json;
           } catch (err) {
             console.error(err.message);
           }
    },
    delete: async url => {
      const option = {
        method: 'DELETE',
        headers: {
          Accept: 'application/json',
        },
      };
     try {
            const res = await fetch(url, option);
            if(!res.ok){
            throw new Error(`응답오류 : ${res.status}`);
            }
            const json = await res.json();
            return json;
          } catch (err) {
            console.error(err.message);
          }
    },
  };