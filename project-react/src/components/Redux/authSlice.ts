import { createSlice, PayloadAction } from '@reduxjs/toolkit';


interface User {
  id: string;
  email: string;
  isAdmin: boolean;
  firstName?: string;
  lastName?: string;
  username?:string;
  city?:string;
  country?:string;
  street?:string;
  number?:string;
}


interface UserState {
  user: User | null; 
}

const initialState: UserState = {
  user: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    login(state, action: PayloadAction<User>) { 
      state.user = action.payload;
    },
    logout(state) {
      state.user = null;
    },
  },
});

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;
