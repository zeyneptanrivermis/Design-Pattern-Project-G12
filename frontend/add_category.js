// 🆕 KATEGORİ EKLEME FONKSİYONLARI

window.selectedCategoryPath = [];

window.loadCategoryLevels = function() {
  const parentSelect = document.getElementById('parent-cat-select');
  const levelsContainer = document.getElementById('category-levels-container');
  
  if (!parentSelect || !levelsContainer) {
    console.error('❌ parent-cat-select or category-levels-container not found!');
    return;
  }

  const selectedParent = parentSelect.value;
  
  // Dynamic dropdown'ları temizle
  levelsContainer.innerHTML = '';
  window.selectedCategoryPath = [];
  
  if (!selectedParent) {
    window.updateCategoryPath();
    return;
  }

  // Seçilen parent'ı path'e ekle
  window.selectedCategoryPath = [selectedParent];
  
  const state = window.inventoryState || JSON.parse(localStorage.getItem('inventoryState')) || {};
  const currentCategory = state[selectedParent];

  console.log('🔍 Selected parent:', selectedParent, 'Type:', typeof currentCategory);

  // Eğer obje ise (alt kategorisi varsa), dropdown ekle
  if (currentCategory && typeof currentCategory === 'object' && !Array.isArray(currentCategory)) {
    window.createCategoryLevelDropdown(currentCategory, 1, levelsContainer);
  }

  window.updateCategoryPath();
};

window.createCategoryLevelDropdown = function(categoryObj, level, container) {
  const newSelectId = `category-level-${level}`;
  const newLabel = document.createElement('label');
  newLabel.setAttribute('for', newSelectId);
  newLabel.innerHTML = `<strong>Level ${level + 1}:</strong>`;
  
  const newSelect = document.createElement('select');
  newSelect.id = newSelectId;
  newSelect.className = 'dynamic-category-level-select';
  newSelect.setAttribute('data-level', level);
  newSelect.style.cssText = 'width: 100%; padding: 8px; margin-bottom: 10px; font-size: 14px;';
  newSelect.innerHTML = '<option value="">-- Select Subcategory --</option>';
  
  // Alt kategorileri ekle
  for (const subCatName in categoryObj) {
    const option = document.createElement('option');
    option.value = subCatName;
    option.textContent = `${'└─'.repeat(level)} ${subCatName}`;
    newSelect.appendChild(option);
  }
  
  // Event listener
  newSelect.addEventListener('change', function() {
    window.handleCategoryLevelChange(this, level, categoryObj, container);
  });
  
  container.appendChild(newLabel);
  container.appendChild(newSelect);
  
  console.log(`✅ Created category level dropdown for level ${level}`);
};

window.handleCategoryLevelChange = function(selectElement, level, parentCategoryObj, container) {
  const selectedValue = selectElement.value;
  
  console.log(`🔍 Category level changed at level ${level}:`, selectedValue);
  
  if (!selectedValue) {
    window.selectedCategoryPath = window.selectedCategoryPath.slice(0, level);
    window.removeCategoryLevelsAfter(level, container);
    window.updateCategoryPath();
    return;
  }
  
  // Yolu güncelle
  window.selectedCategoryPath[level] = selectedValue;
  window.selectedCategoryPath = window.selectedCategoryPath.slice(0, level + 1);
  
  // Sonraki seviyeleri temizle
  window.removeCategoryLevelsAfter(level, container);
  
  const nextCategory = parentCategoryObj[selectedValue];
  
  console.log(`🔍 Level ${level} selected:`, selectedValue, 'Type:', typeof nextCategory, 'isArray:', Array.isArray(nextCategory));
  
  // Eğer obje ise, yeni dropdown ekle
  if (nextCategory && typeof nextCategory === 'object' && !Array.isArray(nextCategory)) {
    window.createCategoryLevelDropdown(nextCategory, level + 1, container);
  } else if (Array.isArray(nextCategory)) {
    console.log('✅ Reached product list (array), can add category here');
  }
  
  window.updateCategoryPath();
};

window.removeCategoryLevelsAfter = function(level, container) {
  const allLevels = container.querySelectorAll('.dynamic-category-level-select');
  
  allLevels.forEach((select) => {
    const selectLevel = parseInt(select.getAttribute('data-level'));
    
    if (selectLevel > level) {
      const label = select.previousElementSibling;
      if (label && label.tagName === 'LABEL') {
        label.remove();
      }
      select.remove();
      console.log(`🗑️ Removed category level ${selectLevel}`);
    }
  });
};

window.updateCategoryPath = function() {
  const pathDisplay = document.getElementById('selected-cat-path');
  if (!pathDisplay) {
    console.warn('⚠️ selected-cat-path not found!');
    return;
  }

  let path = window.selectedCategoryPath.length > 0 
    ? window.selectedCategoryPath.join(' → ') 
    : 'Root (Top-Level)';
  
  pathDisplay.textContent = path;
  pathDisplay.style.color = window.selectedCategoryPath.length > 0 ? '#28a745' : '#0066cc';
  
  console.log('📍 Current category path:', window.selectedCategoryPath);
};

window.simulateAddCategory = function() {
  const catName = document.getElementById('cat-name').value.trim();
  
  if (!catName) {
    alert('❌ Please enter a category name!');
    return;
  }

  const state = window.inventoryState || JSON.parse(localStorage.getItem('inventoryState')) || {};
  
  // Yeni kategori objekti (boş alt kategoriler ve ürün array'ı ile)
  const newCategoryObj = {};
  
  if (window.selectedCategoryPath.length === 0) {
    // Top-level kategori ekle
    if (state[catName]) {
      alert(`❌ Category "${catName}" already exists at top-level!`);
      return;
    }
    
    state[catName] = newCategoryObj;
    console.log(`✅ Top-level category "${catName}" created`);
  } else {
    // Alt kategori ekle
    let current = state;
    let categoryPath = '';
    
    // Yolu takip et
    for (let i = 0; i < window.selectedCategoryPath.length; i++) {
      const key = window.selectedCategoryPath[i];
      categoryPath += (i > 0 ? '.' : '') + key;
      
      if (!current[key]) {
        alert(`❌ Category path not found: ${categoryPath}`);
        return;
      }
      
      current = current[key];
    }
    
    // Eğer current bir array ise (product array), yeni kategoriye ekleyemeyiz
    if (Array.isArray(current)) {
      alert(`❌ Cannot add category here!\n\n"${window.selectedCategoryPath.join(' → ')}" is a product list, not a category container.`);
      return;
    }
    
    // Kategori zaten varsa
    if (current[catName]) {
      alert(`❌ Subcategory "${catName}" already exists under "${window.selectedCategoryPath.join(' → ')}"!`);
      return;
    }
    
    // Yeni kategoriyi ekle
    current[catName] = newCategoryObj;
    categoryPath += `.${catName}`;
    console.log(`✅ Subcategory "${catName}" created at: ${categoryPath}`);
  }
  
  // State'i kaydet
  window.inventoryState = state;
  localStorage.setItem('inventoryState', JSON.stringify(state));
  
  // Başarı mesajı
  const parentPath = window.selectedCategoryPath.length > 0 
    ? window.selectedCategoryPath.join(' → ')
    : 'Top-Level';
  
  alert(`✅ Category "${catName}" added successfully!\n\n📍 Location: ${parentPath}\n\nRedirecting to home page...`);
  
  // Ana sayfaya yönlendir
  setTimeout(function() {
    if (typeof loadPage === 'function') {
      loadPage('home');
    } else {
      window.location.href = '/index.html';
      window.location.reload();
    }
  }, 500);
};

window.loadCategoryParentDropdown = function() {
  const state = window.inventoryState || JSON.parse(localStorage.getItem('inventoryState')) || {};
  const parentSelect = document.getElementById('parent-cat-select');
  
  if (!parentSelect) {
    console.error('❌ parent-cat-select element not found!');
    return;
  }

  console.log('🔄 Loading category parent dropdown...', state);

  // Mevcut seçenekleri temizle (ilk option hariç)
  while (parentSelect.options.length > 1) {
    parentSelect.remove(1);
  }

  // 🔧 DÜZELTME: State boşsa uyar
  if (Object.keys(state).length === 0) {
    console.warn('⚠️ inventoryState is empty! Please initialize sample data first.');
    const option = document.createElement('option');
    option.value = '';
    option.textContent = '⚠️ No categories (Go to Add Product → Initialize Sample Data)';
    parentSelect.appendChild(option);
    return;
  }

  // Ana kategorileri ekle
  for (const categoryName in state) {
    const option = document.createElement('option');
    option.value = categoryName;
    option.textContent = `📦 ${categoryName}`;
    parentSelect.appendChild(option);
    console.log('✅ Added category to dropdown:', categoryName);
  }
};

