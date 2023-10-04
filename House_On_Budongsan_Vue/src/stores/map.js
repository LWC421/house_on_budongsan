import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export default defineStore('map', () => {
  const map = ref(null)
  const scriptLoaded = ref(false)
  const centerLat = ref(0)
  const centerLng = ref(0)

  return { map, scriptLoaded, centerLat, centerLng }
})
