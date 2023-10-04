// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Vuetify
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'

const myTheme = {
  dark: false,
  colors: {
    primary: '#E6B17E',
    secondary: '#C56E33'
  }
}

export default createVuetify({
  theme: {
    defaultTheme: 'myTheme',
    themes: {
      myTheme
    }
  },

  aliases: {
    VBtnPrimary: components.VBtn,
    VBtnSecondary: components.VBtn
  },
  defaults: {
    VBtnPrimary: {
      color: 'primary',
      variant: 'flat'
    },
    VBtnSecondary: {
      color: 'secondary',
      variant: 'flat'
    }
  },

  icons: {
    defaultSet: 'mdi' // This is already the default value - only for display purposes
  }
})
