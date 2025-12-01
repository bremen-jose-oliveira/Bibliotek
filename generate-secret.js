const appleSignin = require('apple-signin-auth');

const clientSecret = appleSignin.getClientSecret({
  clientID: 'com.jose-oliv.mypersonallibraryfront.si',
  teamID: 'YJYA3WJVT5',
  privateKeyPath: './AuthKey_3NJ23GPMBU.p8',  // Replace with your filename
  keyIdentifier: '3NJ23GPMBU',                // Replace with your new Key ID
  expAfter: 15777000                           // 180 days in seconds
});

console.log('client_secret:\n', clientSecret);
